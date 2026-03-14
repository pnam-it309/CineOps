package service.cinema.be.core.admin.quanlynhanvien.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.NhanVien;
import service.cinema.be.repository.NhanVienRepository;

import java.util.List;

@Repository
public interface AdNhanVienRepository extends NhanVienRepository {

    @Query("SELECT n FROM NhanVien n " +
            "LEFT JOIN n.taiKhoan tk " +
            "LEFT JOIN tk.phanQuyen p " +
            "WHERE (:search IS NULL OR :search = '' " +
            "OR n.tenNhanVien LIKE %:search% " +
            "OR n.maNhanVien LIKE %:search% " +
            "OR n.soDienThoai LIKE %:search% " +
            "OR tk.email LIKE %:search%) " +
            "AND (:idPhanQuyen IS NULL OR :idPhanQuyen = '' OR p.id = :idPhanQuyen) " +
            "AND (:trangThai IS NULL OR n.trangThai = :trangThai) " +
            "AND (:gioiTinh IS NULL OR n.gioiTinh = :gioiTinh) " +
            "ORDER BY n.ngayTao DESC")
    Page<NhanVien> searchNhanVien(
            @Param("search") String search,
            @Param("idPhanQuyen") String idPhanQuyen,
            @Param("trangThai") Integer trangThai,
            @Param("gioiTinh") Integer gioiTinh,
            Pageable pageable);

    @Query("SELECT n.maNhanVien FROM NhanVien n WHERE n.maNhanVien LIKE 'NV%' ORDER BY n.maNhanVien DESC LIMIT 1")
    String findMaxMaNhanVien();

    @Query("SELECT COUNT(tk) > 0 FROM TaiKhoan tk WHERE tk.email = :email")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT COUNT(tk) > 0 FROM TaiKhoan tk JOIN NhanVien nv ON nv.taiKhoan = tk WHERE tk.email = :email AND nv.id <> :id")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") String id);

    boolean existsBySoDienThoai(String soDienThoai);
    boolean existsBySoDienThoaiAndIdNot(String soDienThoai, String id);

    boolean existsByCccd(String cccd);
    boolean existsByCccdAndIdNot(String cccd, String id);

    @Query("SELECT DISTINCT n.chucVu FROM NhanVien n WHERE n.chucVu IS NOT NULL AND TRIM(n.chucVu) <> ''")
    List<String> findDistinctChucVu();
}