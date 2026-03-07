package service.cinema.be.core.admin.quanlyve.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.GiaVeChiTiet;

import java.util.Optional;

// ĐỔI Long -> String
@Repository
public interface AdGiaVeChiTietRepository extends JpaRepository<GiaVeChiTiet, String> {

    @Query("SELECT g.giaTien FROM GiaVeChiTiet g " +
            "WHERE g.loaiKhachHang.id = :idLoaiKhach " +
            "AND g.loaiNgay.id = :idLoaiNgay " +
            "AND g.loaiGhe.id = :idLoaiGhe " +
            "AND g.khungGio.id = :idKhungGio " +
            "AND g.trangThai = 1")
    Optional<Double> timGiaPhuThuTheoTieuChi(
            @Param("idLoaiKhach") String idLoaiKhach, // ĐỔI Long -> String
            @Param("idLoaiNgay") String idLoaiNgay,   // ĐỔI Long -> String
            @Param("idLoaiGhe") String idLoaiGhe,     // ĐỔI Long -> String
            @Param("idKhungGio") String idKhungGio    // ĐỔI Long -> String
    );
}