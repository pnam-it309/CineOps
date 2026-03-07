package service.cinema.be.infrastructure.security.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.Token;
import service.cinema.be.repository.TokenRepository;

import java.util.Optional;

@Repository
public interface RefreshTokenAuthRepository extends TokenRepository {

    @Query("SELECT t FROM Token t " +
            "LEFT JOIN FETCH t.taiKhoan tk " +
            "LEFT JOIN FETCH tk.phanQuyen " +
            "WHERE t.maToken = :maToken")
    Optional<Token> findByMaToken(@Param("maToken") String maToken);

    @Query("SELECT t FROM Token t WHERE t.taiKhoan.id = :accountId")
    Optional<Token> findByTaiKhoanId(@Param("accountId") String accountId);

    @Query("DELETE FROM Token t WHERE t.taiKhoan.id = :accountId")
    void deleteByTaiKhoanId(@Param("accountId") String accountId);
}
