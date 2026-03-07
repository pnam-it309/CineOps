package service.cinema.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.KhachHang;

import service.cinema.be.entity.TaiKhoan;

import org.springframework.data.jpa.repository.EntityGraph;
import java.util.Optional;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, String> {
    @EntityGraph(attributePaths = {"taiKhoan"})
    Optional<KhachHang> findByTaiKhoan(TaiKhoan taiKhoan);
    
    Optional<KhachHang> findByMaKhachHang(String maKhachHang);
}
