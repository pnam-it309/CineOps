package service.cinema.be.infrastructure.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.cinema.be.entity.Token;
import service.cinema.be.entity.KhachHang;
import service.cinema.be.entity.NhanVien;
import service.cinema.be.entity.TaiKhoan;
import service.cinema.be.infrastructure.security.repository.RefreshTokenAuthRepository;
import service.cinema.be.infrastructure.security.user.UserPrincipal;
import service.cinema.be.repository.KhachHangRepository;
import service.cinema.be.repository.NhanVienRepository;
import service.cinema.be.repository.TaiKhoanRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class TokenService {

    private final long REFRESH_EXPIRED_HOURS = 168; // 7 days

    private final RefreshTokenAuthRepository tokenRepository;
    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;

    @Autowired
    public TokenService(
            RefreshTokenAuthRepository tokenRepository,
            KhachHangRepository khachHangRepository,
            NhanVienRepository nhanVienRepository,
            TaiKhoanRepository taiKhoanRepository
    ) {
        this.tokenRepository = tokenRepository;
        this.khachHangRepository = khachHangRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.taiKhoanRepository = taiKhoanRepository;
    }

    public Optional<Token> findByToken(String maToken) {
        return tokenRepository.findByMaToken(maToken);
    }

    public Token createRefreshToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return createRefreshToken(userPrincipal.getId());
    }

    public Token createRefreshToken(String userId) {
        TaiKhoan taiKhoan = null;

        // Try finding as Staff/Admin
        Optional<NhanVien> nhanVienOp = nhanVienRepository.findById(userId);
        if (nhanVienOp.isPresent()) {
            taiKhoan = nhanVienOp.get().getTaiKhoan();
        } else {
            // Try finding as Customer
            Optional<KhachHang> khachHangOp = khachHangRepository.findById(userId);
            if (khachHangOp.isPresent()) {
                taiKhoan = khachHangOp.get().getTaiKhoan();
            }
        }

        if (taiKhoan == null) {
            throw new RuntimeException("Account not found for user ID: " + userId);
        }

        Optional<Token> optionalToken = tokenRepository.findByTaiKhoanId(taiKhoan.getId());
        Token token = optionalToken.orElse(new Token());
        token.setTaiKhoan(taiKhoan);
        return saveToken(token);
    }

    private Token saveToken(Token token) {
        token.setNgayHetHan(LocalDateTime.now().plusHours(REFRESH_EXPIRED_HOURS));
        token.setMaToken(UUID.randomUUID().toString());
        token.setTrangThai(1);
        return tokenRepository.save(token);
    }

    public Token verifyExpiration(Token token) {
        if (token.getNgayHetHan().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(token);
            return null;
        }
        return token;
    }
}
