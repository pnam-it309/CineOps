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
import org.springframework.transaction.annotation.Transactional;
import service.cinema.be.core.common.request.LoginRequest;
import service.cinema.be.core.common.response.ApiResponse;
import service.cinema.be.core.common.response.AuthResponse;
import service.cinema.be.infrastructure.constant.MappingConstants;
import service.cinema.be.infrastructure.security.service.TokenService;
import service.cinema.be.infrastructure.security.service.TokenProvider;
import service.cinema.be.infrastructure.security.user.UserPrincipal;
import service.cinema.be.entity.Token;
import service.cinema.be.entity.KhachHang;
import service.cinema.be.entity.NhanVien;
import service.cinema.be.core.common.request.TokenRefreshRequest;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(MappingConstants.API_AUTH_PREFIX)
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final TokenService tokenService;

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
            String refreshToken = tokenService.createRefreshToken(authentication).getMaToken();

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

    /**
     * POST /api/v1/auth/refresh
     * Refresh access token using refresh token
     */
    @Transactional
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        log.info("Yêu cầu refresh token: {}", requestRefreshToken);

        Optional<Token> tokenOptional = tokenService.findByToken(requestRefreshToken);
        if (tokenOptional.isEmpty()) {
            return ResponseEntity.status(400).body(ApiResponse.error(400, "Refresh token không tồn tại!"));
        }

        Token token = tokenService.verifyExpiration(tokenOptional.get());
        if (token == null) {
            return ResponseEntity.status(400).body(ApiResponse.error(400, "Refresh token đã hết hạn!"));
        }

        String accessToken;
        String email;
        String fullName;
        String role;

        try {
            if (token.getKhachHang() != null) {
                KhachHang khachHang = token.getKhachHang();
                email = khachHang.getEmail();
                fullName = khachHang.getTenKhachHang();
                role = "ROLE_CUSTOMER";
                accessToken = tokenProvider.createToken(email, role, khachHang.getId());
            } else if (token.getNhanVien() != null) {
                NhanVien nhanVien = token.getNhanVien();
                email = nhanVien.getEmail();
                fullName = nhanVien.getTenNhanVien();
                // Truy cập PhanQuyen để tránh LazyInitializationException
                role = (nhanVien.getPhanQuyen() != null) ? nhanVien.getPhanQuyen().getMaPhanQuyen() : "ROLE_STAFF";
                accessToken = tokenProvider.createToken(email, role, nhanVien.getId());
            } else {
                return ResponseEntity.status(400).body(ApiResponse.error(400, "Token không hợp lệ!"));
            }

            AuthResponse authResponse = AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(requestRefreshToken)
                    .email(email)
                    .fullName(fullName)
                    .role(role)
                    .build();

            return ResponseEntity.ok(ApiResponse.success(authResponse, "Refresh token thành công"));
        } catch (Exception e) {
            log.error("Lỗi khi xử lý nạp lại token: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(ApiResponse.error(500, "Lỗi server: " + e.getMessage()));
        }
    }
}
