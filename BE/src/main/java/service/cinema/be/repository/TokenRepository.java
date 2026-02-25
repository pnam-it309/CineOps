package service.cinema.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {
}
