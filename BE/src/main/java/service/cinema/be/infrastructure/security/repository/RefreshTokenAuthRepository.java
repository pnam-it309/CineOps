package service.cinema.be.infrastructure.security.repository;


import service.cinema.be.entity.RefreshToken;
import service.cinema.be.repository.RefreshTokenRepository;

import java.util.Optional;

public interface RefreshTokenAuthRepository extends RefreshTokenRepository {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    Optional<RefreshToken> findByUserId(String userId);
}
