package service.cinema.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.KhungGio;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface KhungGioRepository extends JpaRepository<KhungGio, String> {
    
    @Query("SELECT k FROM KhungGio k WHERE k.trangThai = 1 ORDER BY k.thuTrongTuan, k.gioBatDau")
    List<KhungGio> findAllActive();
    
    @Query("SELECT k FROM KhungGio k WHERE k.trangThai = 1 AND k.thuTrongTuan = :thu ORDER BY k.gioBatDau")
    List<KhungGio> findByThuTrongTuan(Integer thu);
    
    @Query("SELECT k FROM KhungGio k WHERE k.trangThai = 1 AND k.thuTrongTuan = :thu " +
           "AND :gioBatDau BETWEEN k.gioBatDau AND k.gioKetThuc")
    List<KhungGio> findByThuAndGio(Integer thu, LocalTime gioBatDau);
    
    @Query("SELECT k FROM KhungGio k WHERE k.trangThai = 1 AND k.laKhungGioVang = true " +
           "AND k.thuTrongTuan = :thu " +
           "AND :gioBatDau BETWEEN k.gioBatDau AND k.gioKetThuc")
    List<KhungGio> findKhungGioVang(Integer thu, LocalTime gioBatDau);
}
