package service.cinema.be.core.admin.quanlynhanvien.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.NhanVien;
import service.cinema.be.repository.NhanVienRepository;

import java.util.List;

@Repository
public interface AdNhanVienRepository extends NhanVienRepository {

    @Query(value = "SELECT n.* FROM nhan_vien n " +
            "LEFT JOIN phan_quyen p ON n.id_phan_quyen = p.id " +
            "WHERE (:search IS NULL OR :search = '' " +
            "OR n.ten_nhan_vien LIKE CONCAT('%', :search, '%') " +
            "OR n.ma_nhan_vien LIKE CONCAT('%', :search, '%') " +
            "OR n.so_dien_thoai LIKE CONCAT('%', :search, '%') " +
            "OR n.email LIKE CONCAT('%', :search, '%')) " +
            "AND (:idPhanQuyen IS NULL OR :idPhanQuyen = '' OR n.id_phan_quyen = :idPhanQuyen) " +
            "AND (:trangThai IS NULL OR n.trang_thai = :trangThai) " +
            "ORDER BY n.ngay_tao DESC",
            nativeQuery = true)
    List<NhanVien> searchNhanVien(
            @Param("search") String search,
            @Param("idPhanQuyen") String idPhanQuyen,
            @Param("trangThai") Integer trangThai);

    // --- BỔ SUNG: Truy vấn để lấy mã nhân viên lớn nhất hiện tại ---
    @Query("SELECT n.maNhanVien FROM NhanVien n WHERE n.maNhanVien LIKE 'NV%' ORDER BY n.maNhanVien DESC LIMIT 1")
    String findMaxMaNhanVien();

    // Kiểm tra trùng lặp email
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, String id);

    // Kiểm tra trùng lặp số điện thoại
    boolean existsBySoDienThoai(String soDienThoai);
    boolean existsBySoDienThoaiAndIdNot(String soDienThoai, String id);

    // Kiểm tra trùng lặp CCCD
    boolean existsByCccd(String cccd);
    boolean existsByCccdAndIdNot(String cccd, String id);
}