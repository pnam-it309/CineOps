package service.cinema.be.core.admin.quanlykhachhang.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.KhachHang;
import service.cinema.be.repository.KhachHangRepository;

import java.util.List;

@Repository
public interface AdKhachHangRepository extends KhachHangRepository {

    @Query(value = "SELECT * FROM khach_hang k WHERE " +
            "(:search IS NULL OR :search = '' " +
            "OR k.ten_khach_hang LIKE CONCAT('%', :search, '%') " +
            "OR k.sdt LIKE CONCAT('%', :search, '%') " +
            "OR k.email LIKE CONCAT('%', :search, '%')) " +
            "AND (:trangThai IS NULL OR k.trang_thai = :trangThai) " +
            "ORDER BY k.ngay_tao DESC",
            nativeQuery = true)
    List<KhachHang> findAllBySearchNative(
            @Param("search") String search,
            @Param("trangThai") Integer trangThai);

    // Lấy mã khách hàng lớn nhất để sinh mã tự động
    @Query("SELECT k.maKhachHang FROM KhachHang k WHERE k.maKhachHang LIKE 'KH%' ORDER BY k.maKhachHang DESC LIMIT 1")
    String findMaxMaKhachHang();

    boolean existsByEmail(String email);
    boolean existsBySdt(String sdt);
    boolean existsByEmailAndIdNot(String email, String id);
    boolean existsBySdtAndIdNot(String sdt, String id);
}