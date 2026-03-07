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
import service.cinema.be.entity.TaiKhoan;
import service.cinema.be.repository.KhachHangRepository;
import service.cinema.be.repository.NhanVienRepository;
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
    private final NhanVienRepository nhanVienRepository;
    private final KhachHangRepository khachHangRepository;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMat_khau())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            String accessToken = tokenProvider.createToken(
                    userPrincipal.getEmail(),
                    userPrincipal.getRole(),
                    userPrincipal.getId()
            );

            String refreshToken = tokenService.createRefreshToken(authentication).getMaToken();

            AuthResponse authResponse = AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .email(userPrincipal.getEmail())
                    .fullName(userPrincipal.getFullName())
                    .role(userPrincipal.getRole())
                    .build();

            return ResponseEntity.ok(ApiResponse.success(authResponse, "Đăng nhập thành công"));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "Email hoặc mật khẩu không đúng"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error(500, "Đăng nhập thất bại: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(ApiResponse.success(null, "Đăng xuất thành công"));
    }

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

    @Transactional
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        Optional<Token> tokenOptional = tokenService.findByToken(requestRefreshToken);
        if (tokenOptional.isEmpty()) {
            return ResponseEntity.status(400).body(ApiResponse.error(400, "Refresh token không tồn tại!"));
        }

        Token token = tokenService.verifyExpiration(tokenOptional.get());
        if (token == null) {
            return ResponseEntity.status(400).body(ApiResponse.error(400, "Refresh token đã hết hạn!"));
        }

        TaiKhoan taiKhoan = token.getTaiKhoan();
        if (taiKhoan == null) {
            return ResponseEntity.status(400).body(ApiResponse.error(400, "Token không hợp lệ (không gắn với tải khoản nào)!"));
        }

        String accessToken;
        String email = taiKhoan.getEmail();
        String fullName = "User";
        String role = "ROLE_USER";
        String profileId = "";

        // Try to find the associated entity (Staff or Customer)
        Optional<NhanVien> nhanVienOp = nhanVienRepository.findByTaiKhoan(taiKhoan);
        if (nhanVienOp.isPresent()) {
            NhanVien nhanVien = nhanVienOp.get();
            fullName = nhanVien.getTenNhanVien();
            role = (taiKhoan.getPhanQuyen() != null) ? taiKhoan.getPhanQuyen().getMaPhanQuyen() : "ROLE_STAFF";
            profileId = nhanVien.getId();
        } else {
            Optional<KhachHang> khachHangOp = khachHangRepository.findByTaiKhoan(taiKhoan);
            if (khachHangOp.isPresent()) {
                KhachHang khachHang = khachHangOp.get();
                fullName = khachHang.getTenKhachHang();
                role = "ROLE_CUSTOMER";
                profileId = khachHang.getId();
            } else {
                 return ResponseEntity.status(400).body(ApiResponse.error(400, "Không tìm thấy hồ sơ liên kết!"));
            }
        }

        accessToken = tokenProvider.createToken(email, role, profileId);

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(requestRefreshToken)
                .email(email)
                .fullName(fullName)
                .role(role)
                .build();

        return ResponseEntity.ok(ApiResponse.success(authResponse, "Refresh token thành công"));
    }
}
