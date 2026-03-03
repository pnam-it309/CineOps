package service.cinema.be.core.admin.quanlyhoadon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.HoaDonChiTiet;

import java.util.List;

@Repository
public interface AdHoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, String> {

    // Tìm tất cả chi tiết vé/đồ ăn thuộc về 1 mã ID hóa đơn
    List<HoaDonChiTiet> findByHoaDonId(String idHoaDon);
}