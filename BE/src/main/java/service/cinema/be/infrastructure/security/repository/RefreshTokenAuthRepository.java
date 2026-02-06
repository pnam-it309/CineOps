package service.cinema.be.infrastructure.security.repository;

import org.springframework.stereotype.Repository;
import service.cinema.be.entity.RefreshToken;
import service.cinema.be.repository.RefreshTokenRepository;

import java.util.Optional;

/**
 * Repository for RefreshToken authentication operations
 * Extends base RefreshTokenRepository with auth-specific methods
 */
@Repository
public interface RefreshTokenAuthRepository extends RefreshTokenRepository {
    
    /**
     * Find refresh token by token string
     */
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    /**
     * Find refresh token by user ID
     */
    Optional<RefreshToken> findByUserId(String userId);

    /**
     * Delete all refresh tokens for a user
     */
    void deleteByUserId(String userId);
}
