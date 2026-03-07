package service.cinema.be.core.admin.quanlyhoadon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import service.cinema.be.core.admin.quanlyhoadon.dto.response.AdHoaDonResponse;
import service.cinema.be.entity.HoaDon;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdHoaDonRepository extends JpaRepository<HoaDon, String>, JpaSpecificationExecutor<HoaDon> {

    // Doanh thu các hóa đơn đã thanh toán
    @Query("SELECT SUM(h.tongTienThanhToan) FROM HoaDon h WHERE h.trangThai = 1")
    BigDecimal sumTotalRevenue();

    // Đếm tổng số hóa đơn theo trạng thái
    long countByTrangThai(Integer trangThai);

    // Hỗ trợ tìm kiếm bằng UUID hoặc mã hóa đơn (ví dụ: HD-1234)
    java.util.Optional<HoaDon> findByIdOrMaHoaDon(String id, String maHoaDon);

    /**
     * Fix #5 — Tìm các hóa đơn PENDING đã quá hạn booking timeout.
     * Dùng bởi BookingTimeoutScheduler để auto-release ghế.
     */
    @Query("SELECT h FROM HoaDon h WHERE h.trangThai = :trangThai AND h.thoiGianHetHan < :now")
    List<HoaDon> findByTrangThaiAndThoiGianHetHanBefore(
            @Param("trangThai") Integer trangThai,
            @Param("now") LocalDateTime now
    );
    @Query("SELECT new service.cinema.be.core.admin.quanlyhoadon.dto.response.AdHoaDonResponse(" +
            "h.id, h.maHoaDon, h.tongTienThanhToan, h.trangThai, h.ngayTao, " +
            "COALESCE(k.tenKhachHang, 'Khách lẻ'), " +
            "COALESCE(k.sdt, 'N/A'), " +
            "COALESCE(t.email, 'N/A'), " + // Vị trí cho trường email
            "COALESCE(t.email, 'N/A')) " + // Vị trí cho trường taiKhoan (vì 2 cái là 1)
            "FROM HoaDon h " +
            "LEFT JOIN h.khachHang k " +
            "LEFT JOIN k.taiKhoan t " +
            "WHERE h.id = :id")
    AdHoaDonResponse getHoaDonResponseById(@Param("id") String id);
}