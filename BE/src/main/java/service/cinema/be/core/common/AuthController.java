package service.cinema.be.core.common;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import service.cinema.be.core.common.request.LoginRequest;
import service.cinema.be.core.common.response.ApiResponse;
import service.cinema.be.core.common.response.AuthResponse;
import service.cinema.be.infrastructure.constant.MappingConstants;
import service.cinema.be.infrastructure.security.service.RefreshTokenService;
import service.cinema.be.infrastructure.security.service.TokenProvider;
import service.cinema.be.infrastructure.security.user.UserPrincipal;

@Slf4j
@RestController
@RequestMapping(MappingConstants.API_AUTH_PREFIX)
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    /**
     * POST /api/v1/auth/login
     * Đăng nhập bằng email + mật khẩu, trả về JWT
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            // Spring Security xác thực email + password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            // Tạo JWT access token
            String accessToken = tokenProvider.createToken(
                    userPrincipal.getEmail(),
                    userPrincipal.getRole(),
                    userPrincipal.getId()
            );

            // Tạo refresh token
            String refreshToken = refreshTokenService.createRefreshToken(authentication).getRefreshToken();

            AuthResponse authResponse = AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .email(userPrincipal.getEmail())
                    .fullName(userPrincipal.getFullName())
                    .role(userPrincipal.getRole())
                    .build();

            log.info("User đăng nhập thành công: {} | role: {}", userPrincipal.getEmail(), userPrincipal.getRole());

            return ResponseEntity.ok(ApiResponse.success(authResponse, "Đăng nhập thành công"));

        } catch (BadCredentialsException e) {
            log.warn("Đăng nhập thất bại - sai mật khẩu: {}", request.getEmail());
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "Email hoặc mật khẩu không đúng"));
        } catch (Exception e) {
            log.error("Lỗi đăng nhập: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error(500, "Đăng nhập thất bại: " + e.getMessage()));
        }
    }

    /**
     * POST /api/v1/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(ApiResponse.success(null, "Đăng xuất thành công"));
    }

    /**
     * GET /api/v1/auth/me
     * Trả về thông tin user hiện tại từ JWT
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse>> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "Chưa đăng nhập"));
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        AuthResponse authResponse = AuthResponse.builder()
                .email(userPrincipal.getEmail())
                .fullName(userPrincipal.getFullName())
                .role(userPrincipal.getRole())
                .build();

        return ResponseEntity.ok(ApiResponse.success(authResponse));
    }
}
