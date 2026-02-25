package service.cinema.be.infrastructure.security.repository;

import org.springframework.stereotype.Repository;
import service.cinema.be.entity.Token;
import service.cinema.be.repository.TokenRepository;

import java.util.Optional;

@Repository
public interface RefreshTokenAuthRepository extends TokenRepository {
    Optional<Token> findByMaToken(String maToken);
    Optional<Token> findByKhachHangId(String khachHangId);
    void deleteByKhachHangId(String khachHangId);
}
