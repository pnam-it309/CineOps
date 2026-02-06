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
import service.cinema.be.entity.PhanQuyen;
import service.cinema.be.entity.TaiKhoan;
import service.cinema.be.infrastructure.constant.AuthProvider;
import service.cinema.be.infrastructure.constant.CookieConstant;
import service.cinema.be.infrastructure.constant.EntityRole;
import service.cinema.be.infrastructure.constant.OAuth2Constant;
import service.cinema.be.infrastructure.exception.OAuth2AuthenticationProcessingException;
import service.cinema.be.infrastructure.security.oauth2.user.GithubOAuth2UserInfo;
import service.cinema.be.infrastructure.security.oauth2.user.OAuth2UserInfo;
import service.cinema.be.infrastructure.security.oauth2.user.OAuth2UserInfoFactory;
import service.cinema.be.infrastructure.security.user.UserPrincipal;
import service.cinema.be.repository.RoleRepository;
import service.cinema.be.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

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

        // Fetch GitHub email if needed
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

        // Get role from cookie (set by frontend during OAuth2 redirect)
        Optional<Cookie> cookieOpRole = CookieUtils.getCookie(httpServletRequest,
                OAuth2Constant.SCREEN_FOR_ROLE_COOKIE_NAME);

        String roleStr = cookieOpRole.map(Cookie::getValue).orElse("ROLE_USER"); // Default to USER
        boolean isRegister = CookieUtils.getCookie(httpServletRequest,
                OAuth2Constant.REGISTER_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .map(val -> "true".equalsIgnoreCase(val))
                .orElse(false);

        log.info("Processing OAuth2 user: email={}, role={}, isRegister={}", 
                oAuth2UserInfo.getEmail(), roleStr, isRegister);

        return processUserByRole(oAuth2UserInfo, roleStr, isRegister, 
                oAuth2UserRequest.getClientRegistration().getRegistrationId());
    }

    private OAuth2User processUserByRole(OAuth2UserInfo oAuth2UserInfo, String roleStr, 
                                          boolean isRegister, String providerId) {
        Optional<KhachHang> khachHangOptional = userRepository.findByEmailWithRoles(
                oAuth2UserInfo.getEmail());

        if (khachHangOptional.isPresent()) {
            // Existing customer - update OAuth2 info
            KhachHang khachHang = khachHangOptional.get();
            updateKhachHangFromOAuth2(khachHang, oAuth2UserInfo, providerId);
            userRepository.save(khachHang);

            UserPrincipal principal = UserPrincipal.createFromKhachHang(khachHang);
            principal.setAttributes(oAuth2UserInfo.getAttributes());
            return principal;

        } else if (isRegister) {
            // New customer registration via OAuth2
            KhachHang newKhachHang = createKhachHangFromOAuth2(oAuth2UserInfo, roleStr, providerId);
            newKhachHang = userRepository.save(newKhachHang);

            UserPrincipal principal = UserPrincipal.createFromKhachHang(newKhachHang);
            principal.setAttributes(oAuth2UserInfo.getAttributes());
            return principal;

        } else {
            // Customer not found and not registering
            log.error("Customer not found and not registering: {}", oAuth2UserInfo.getEmail());
            CookieUtils.addCookie(httpServletResponse, CookieConstant.ACCOUNT_NOT_EXIST,
                    CookieConstant.ACCOUNT_NOT_EXIST);
            throw new OAuth2AuthenticationProcessingException(CookieConstant.ACCOUNT_NOT_EXIST);
        }
    }

    private KhachHang createKhachHangFromOAuth2(OAuth2UserInfo oAuth2UserInfo, String roleStr, String providerId) {
        // Create TaiKhoan first
        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setId(UUID.randomUUID().toString());
        taiKhoan.setTenTaiKhoan(oAuth2UserInfo.getEmail());
        taiKhoan.setMatKhau(null); // OAuth2 users don't have password
        taiKhoan.setTrangThai(1); // Active

        // Find and assign role
        String maPhanQuyen = convertRoleStringToMaPhanQuyen(roleStr);
        Optional<PhanQuyen> phanQuyenOptional = roleRepository.findByMaPhanQuyen(maPhanQuyen);
        if (phanQuyenOptional.isEmpty()) {
            log.error("Role not found: {}", maPhanQuyen);
            throw new OAuth2AuthenticationProcessingException("Role not found: " + maPhanQuyen);
        }
        taiKhoan.setPhanQuyen(phanQuyenOptional.get());

        // Create KhachHang
        KhachHang khachHang = new KhachHang();
        khachHang.setId(UUID.randomUUID().toString());
        khachHang.setTaiKhoan(taiKhoan);
        khachHang.setMaKhachHang("KH" + System.currentTimeMillis());
        khachHang.setTenKhachHang(oAuth2UserInfo.getName() != null ? oAuth2UserInfo.getName() : oAuth2UserInfo.getEmail());
        khachHang.setEmail(oAuth2UserInfo.getEmail());
        khachHang.setTrangThai(1); // Active

        return khachHang;
    }

    private void updateKhachHangFromOAuth2(KhachHang khachHang, OAuth2UserInfo oAuth2UserInfo, String providerId) {
        boolean updated = false;
        
        if (oAuth2UserInfo.getName() != null && !oAuth2UserInfo.getName().isBlank()) {
            khachHang.setTenKhachHang(oAuth2UserInfo.getName());
            updated = true;
        }
        
        if (updated) {
            log.debug("Updated OAuth2 customer info for: {}", khachHang.getEmail());
        }
    }

    /**
     * Convert role string to ma_phan_quyen
     * Example: "ROLE_USER" -> "role_user", "CUSTOMER" -> "role_user"
     */
    private String convertRoleStringToMaPhanQuyen(String roleStr) {
        try {
            EntityRole entityRole = EntityRole.valueOf(roleStr.toUpperCase());
            return entityRole.name().toLowerCase();
        } catch (Exception e) {
            log.warn("Invalid role: {}, defaulting to ROLE_USER", roleStr);
            return "role_user";
        }
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