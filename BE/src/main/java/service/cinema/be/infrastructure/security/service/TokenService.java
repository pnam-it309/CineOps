package service.cinema.be.infrastructure.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import service.cinema.be.entity.Token;
import service.cinema.be.entity.KhachHang;
import service.cinema.be.infrastructure.security.repository.RefreshTokenAuthRepository;
import service.cinema.be.infrastructure.security.user.UserPrincipal;
import service.cinema.be.repository.KhachHangRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TokenService {

    private final long REFRESH_EXPIRED_HOURS = 6;

    private final RefreshTokenAuthRepository tokenRepository;
    private final KhachHangRepository khachHangRepository;

    @Autowired
    public TokenService(
            RefreshTokenAuthRepository tokenRepository,
            KhachHangRepository khachHangRepository
    ) {
        this.tokenRepository = tokenRepository;
        this.khachHangRepository = khachHangRepository;
    }

    public Optional<Token> findByToken(String maToken) {
        return tokenRepository.findByMaToken(maToken);
    }

    public Token createRefreshToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return createRefreshToken(userPrincipal.getId());
    }

    public Token createRefreshToken(String khachHangId) {
        Optional<Token> optionalToken = tokenRepository.findByKhachHangId(khachHangId);
        KhachHang khachHang = khachHangRepository.findById(khachHangId).orElse(null);

        if (optionalToken.isPresent()) {
            Token token = optionalToken.get();
            token.setNgayHetHan(LocalDateTime.now().plusHours(REFRESH_EXPIRED_HOURS));
            token.setMaToken(UUID.randomUUID().toString());
            token.setTrangThai(1);
            return tokenRepository.save(token);
        }

        Token token = new Token();
        token.setKhachHang(khachHang);
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
