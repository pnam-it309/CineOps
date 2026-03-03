package service.cinema.be.core.admin.quanlyhoadon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.HoaDon;

import java.math.BigDecimal;

@Repository
public interface AdHoaDonRepository extends JpaRepository<HoaDon, String>, JpaSpecificationExecutor<HoaDon> {

    // Câu truy vấn tính tổng doanh thu của các Hóa Đơn đã thanh toán (trạng thái = 1)
    @Query("SELECT SUM(h.tongTienThanhToan) FROM HoaDon h WHERE h.trangThai = 1")
    BigDecimal sumTotalRevenue();

    // Đếm tổng số hóa đơn trong hệ thống
    long countByTrangThai(Integer trangThai);
}