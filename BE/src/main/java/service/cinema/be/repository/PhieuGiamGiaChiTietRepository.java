package service.cinema.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.PhieuGiamGia;
import service.cinema.be.entity.PhieuGiamGiaChiTiet;

import java.util.List;

@Repository
public interface PhieuGiamGiaChiTietRepository extends JpaRepository<PhieuGiamGiaChiTiet, String> {
    List<PhieuGiamGiaChiTiet> findByPhieuGiamGia(PhieuGiamGia phieuGiamGia);
    void deleteByPhieuGiamGia(PhieuGiamGia phieuGiamGia);
}
