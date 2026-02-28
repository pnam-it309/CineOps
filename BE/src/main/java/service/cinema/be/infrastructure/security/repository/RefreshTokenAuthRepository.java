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
            "LEFT JOIN FETCH t.khachHang k " +
            "LEFT JOIN FETCH t.nhanVien n " +
            "LEFT JOIN FETCH n.phanQuyen " +
            "WHERE t.maToken = :maToken")
    Optional<Token> findByMaToken(@Param("maToken") String maToken);

    Optional<Token> findByKhachHangId(String khachHangId);
    Optional<Token> findByNhanVienId(String nhanVienId);
    void deleteByKhachHangId(String khachHangId);
    void deleteByNhanVienId(String nhanVienId);
}
