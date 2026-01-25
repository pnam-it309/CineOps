package service.cinema.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.cinema.be.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
}
