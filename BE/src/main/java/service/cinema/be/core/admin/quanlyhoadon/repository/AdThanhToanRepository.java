package service.cinema.be.core.admin.quanlyhoadon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.ThanhToan;

import java.util.List;

@Repository
public interface AdThanhToanRepository extends JpaRepository<ThanhToan, String> {

    // Tra cứu lịch sử thanh toán của 1 hóa đơn
    List<ThanhToan> findByHoaDonId(String idHoaDon);
}