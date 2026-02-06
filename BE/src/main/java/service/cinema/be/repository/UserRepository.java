package service.cinema.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.KhachHang;

import java.util.Optional;

/**
 * Repository for KhachHang (Customer) entity
 * Replaces the old UserRepository
 */
@Repository
public interface UserRepository extends JpaRepository<KhachHang, String> {

    /**
     * Find customer by email
     *
     * @param email Customer email
     * @return Optional of KhachHang
     */
    Optional<KhachHang> findByEmail(String email);

    /**
     * Find customer by email with account and role eagerly loaded
     *
     * @param email Customer email
     * @return Optional of KhachHang with TaiKhoan and PhanQuyen
     */
    @Query("SELECT k FROM KhachHang k LEFT JOIN FETCH k.taiKhoan t LEFT JOIN FETCH t.phanQuyen WHERE k.email = :email")
    Optional<KhachHang> findByEmailWithRoles(@Param("email") String email);

    /**
     * Check if email exists
     *
     * @param email Email to check
     * @return true if exists
     */
    boolean existsByEmail(String email);

}
