package service.cinema.be.core.admin.quanlykhachhang.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.KhachHang;
import service.cinema.be.repository.KhachHangRepository;

import java.util.List;

@Repository
public interface AdKhachHangRepository extends KhachHangRepository {

    @Query("SELECT k FROM KhachHang k " +
            "LEFT JOIN k.taiKhoan tk " +
            "WHERE (:search IS NULL OR :search = '' " +
            "OR k.tenKhachHang LIKE %:search% " +
            "OR k.sdt LIKE %:search% " +
            "OR tk.email LIKE %:search%) " +
            "AND (:trangThai IS NULL OR k.trangThai = :trangThai) " +
            "ORDER BY k.ngayTao DESC")
    Page<KhachHang> findAllBySearch(
            @Param("search") String search,
            @Param("trangThai") Integer trangThai,
            Pageable pageable);

    // Lấy mã khách hàng lớn nhất để sinh mã tự động
    @Query("SELECT k.maKhachHang FROM KhachHang k WHERE k.maKhachHang LIKE 'KH%' ORDER BY k.maKhachHang DESC LIMIT 1")
    String findMaxMaKhachHang();

    @Query("SELECT CASE WHEN COUNT(k) > 0 THEN true ELSE false END FROM KhachHang k JOIN k.taiKhoan tk WHERE tk.email = :email")
    boolean existsByEmail(@Param("email") String email);
    
    boolean existsBySdt(String sdt);
    
    @Query("SELECT CASE WHEN COUNT(k) > 0 THEN true ELSE false END FROM KhachHang k JOIN k.taiKhoan tk WHERE tk.email = :email AND k.id != :id")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") String id);
    
    boolean existsBySdtAndIdNot(String sdt, String id);
}