package service.cinema.be.core.admin.quanlysuatchieu.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.SuatChieu;
import service.cinema.be.repository.SuatChieuRepository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AdSuatChieuRepository extends SuatChieuRepository {

    @Query(value = "SELECT sc.* FROM suat_chieu sc JOIN khung_gio kg ON sc.id_khung_gio = kg.id " +
                   "WHERE (:ngay IS NULL OR sc.ngay_chieu = :ngay) " +
                   "AND (:idPC IS NULL OR sc.id_phong_chieu = :idPC) " +
                   "ORDER BY sc.ngay_chieu DESC, kg.gio_bat_dau", nativeQuery = true)
    List<SuatChieu> findByNgayVaPhongNative(@Param("ngay") LocalDate ngay, @Param("idPC") String idPC);

    @Query("SELECT COUNT(s) > 0 FROM SuatChieu s WHERE s.phongChieu.id = :idPC " +
            "AND s.ngayChieu = :ngay " +
            "AND s.id <> :excludeId " +
            "AND ((s.khungGio.gioBatDau < :end AND s.khungGio.gioKetThuc > :start))")
    boolean existsConflict(@Param("idPC") String idPC, 
                          @Param("ngay") LocalDate ngay, 
                          @Param("start") java.time.LocalTime start,
                          @Param("end") java.time.LocalTime end,
                          @Param("excludeId") String excludeId);
}
