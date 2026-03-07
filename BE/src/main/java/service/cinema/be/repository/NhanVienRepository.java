package service.cinema.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.NhanVien;

import service.cinema.be.entity.TaiKhoan;

import org.springframework.data.jpa.repository.EntityGraph;
import java.util.Optional;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, String> {
    @EntityGraph(attributePaths = {"taiKhoan", "taiKhoan.phanQuyen"})
    Optional<NhanVien> findByTaiKhoan(TaiKhoan taiKhoan);
    
    Optional<NhanVien> findByMaNhanVien(String maNhanVien);
}
