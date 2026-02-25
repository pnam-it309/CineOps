package service.cinema.be.infrastructure.security.oauth2;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import service.cinema.be.entity.KhachHang;
import service.cinema.be.infrastructure.constant.AuthProvider;
import service.cinema.be.infrastructure.constant.CookieConstant;
import service.cinema.be.infrastructure.constant.OAuth2Constant;
import service.cinema.be.infrastructure.exception.OAuth2AuthenticationProcessingException;
import service.cinema.be.infrastructure.security.oauth2.user.GithubOAuth2UserInfo;
import service.cinema.be.infrastructure.security.oauth2.user.OAuth2UserInfo;
import service.cinema.be.infrastructure.security.oauth2.user.OAuth2UserInfoFactory;
import service.cinema.be.infrastructure.security.user.UserPrincipal;
import service.cinema.be.repository.KhachHangRepository;
import service.cinema.be.utils.CookieUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;
    private final KhachHangRepository khachHangRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                oAuth2UserRequest.getClientRegistration().getRegistrationId(),
                oAuth2User.getAttributes());

        if (AuthProvider.github.toString().equals(oAuth2UserRequest.getClientRegistration().getRegistrationId())
                && (oAuth2UserInfo.getEmail() == null || oAuth2UserInfo.getEmail().isBlank())) {
            String email = fetchGithubEmail(oAuth2UserRequest);
            if (email != null) {
                oAuth2UserInfo = new GithubOAuth2UserInfo(oAuth2User.getAttributes(), email);
            }
        }

        if (oAuth2UserInfo.getEmail() == null || oAuth2UserInfo.getEmail().isBlank()) {
            CookieUtils.addCookie(httpServletResponse, CookieConstant.ACCOUNT_NOT_EXIST,
                    CookieConstant.ACCOUNT_NOT_EXIST);
            throw new OAuth2AuthenticationProcessingException(CookieConstant.ACCOUNT_NOT_EXIST);
        }

        return processUser(oAuth2UserInfo);
    }

    private OAuth2User processUser(OAuth2UserInfo oAuth2UserInfo) {
        Optional<KhachHang> khachHangOptional = khachHangRepository.findByEmail(oAuth2UserInfo.getEmail());

        KhachHang khachHang;
        if (khachHangOptional.isPresent()) {
            khachHang = khachHangOptional.get();
        } else {
            khachHang = new KhachHang();
            khachHang.setId(UUID.randomUUID().toString());
            khachHang.setMaKhachHang("KH" + System.currentTimeMillis());
            khachHang.setEmail(oAuth2UserInfo.getEmail());
            khachHang.setTenKhachHang(oAuth2UserInfo.getName() != null ? oAuth2UserInfo.getName() : oAuth2UserInfo.getEmail());
            khachHang.setTrangThai(1);
            khachHang = khachHangRepository.save(khachHang);
        }

        UserPrincipal principal = UserPrincipal.createFromKhachHang(khachHang);
        principal.setAttributes(oAuth2UserInfo.getAttributes());
        return principal;
    }

    private String fetchGithubEmail(OAuth2UserRequest userRequest) {
        String token = userRequest.getAccessToken().getTokenValue();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "token " + token);
        HttpEntity<String> entity = new HttpEntity<>("", headers);

        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    "https://api.github.com/user/emails",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    });

            List<Map<String, Object>> emails = response.getBody();
            if (emails != null) {
                for (Map<String, Object> e : emails) {
                    Boolean primary = (Boolean) e.get("primary");
                    Boolean verified = (Boolean) e.get("verified");
                    if (Boolean.TRUE.equals(primary) && Boolean.TRUE.equals(verified)) {
                        return (String) e.get("email");
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to fetch GitHub email", e);
        }
        return null;
    }
}