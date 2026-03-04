package service.cinema.be.core.admin.quanlyhoadon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.HoaDonChiTiet;
import service.cinema.be.entity.Ve;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdHoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, String> {

    // Tìm tất cả chi tiết vé/đồ ăn thuộc về 1 mã ID hóa đơn
    List<HoaDonChiTiet> findByHoaDonId(String idHoaDon);

    /**
     * Fix #7 — Ve back-link to HoaDon.
     * Tìm HoaDon chứa một vé cụ thể (qua bảng trung gian hoa_don_chi_tiet).
     * Dùng khi cần query ngược: "Vé này thuộc hóa đơn nào?"
     */
    @Query("SELECT hdc.hoaDon.id FROM HoaDonChiTiet hdc WHERE hdc.ve.id = :veId")
    Optional<String> findHoaDonIdByVeId(@Param("veId") String veId);

    /**
     * Fix #5 — Lấy tất cả Ve trong một HoaDon (dùng bởi BookingTimeoutScheduler).
     * Chỉ lấy chi tiết loại vé (loai = 0), bỏ qua đồ ăn (loai = 1).
     */
    @Query("SELECT hdc.ve FROM HoaDonChiTiet hdc WHERE hdc.hoaDon.id = :hoaDonId AND hdc.ve IS NOT NULL")
    List<Ve> findVeByHoaDonId(@Param("hoaDonId") String hoaDonId);
}