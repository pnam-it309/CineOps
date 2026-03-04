package service.cinema.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.Phim;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PhimRepository extends JpaRepository<Phim, String> {

    @Query("SELECT p FROM Phim p WHERE p.trangThai = 1 ORDER BY p.ngayKhoiChieu DESC")
    List<Phim> findAllActive();

    // ===== Fix #9 — Auto-update movie status queries =====

    /**
     * Phim đang chiếu: ngayKhoiChieu <= today AND ngayKetThuc >= today
     * Chỉ update những phim đang ở trạng thái 0 (sắp chiếu) để tránh đè lên trạng thái ẩn (3)
     */
    @Modifying
    @Query("UPDATE Phim p SET p.trangThai = 1 " +
           "WHERE p.ngayKhoiChieu <= :today AND p.ngayKetThuc >= :today " +
           "AND p.trangThai = 0")
    int updateTrangThaiDangChieu(@Param("today") LocalDate today);

    /**
     * Phim kết thúc: ngayKetThuc < today
     * Chỉ update những phim đang chiếu (trang_thai = 1)
     */
    @Modifying
    @Query("UPDATE Phim p SET p.trangThai = 2 " +
           "WHERE p.ngayKetThuc < :today AND p.trangThai = 1")
    int updateTrangThaiKetThuc(@Param("today") LocalDate today);

    /**
     * Phim sắp chiếu: ngayKhoiChieu > today
     * Chỉ reset những phim bị nhầm sang đang chiếu
     */
    @Modifying
    @Query("UPDATE Phim p SET p.trangThai = 0 " +
           "WHERE p.ngayKhoiChieu > :today AND p.trangThai = 1")
    int updateTrangThaiSapChieu(@Param("today") LocalDate today);
}
