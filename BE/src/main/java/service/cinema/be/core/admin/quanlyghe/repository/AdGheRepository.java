package service.cinema.be.core.admin.quanlyghe.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.Ghe;
import service.cinema.be.repository.GheRepository;

import java.util.List;

@Repository
public interface AdGheRepository extends GheRepository {

    @Query(value = "SELECT * FROM ghe g WHERE (:idPC IS NULL OR g.id_phong_chieu = :idPC) ORDER BY g.id_phong_chieu, g.so_hang, g.so_cot", nativeQuery = true)
    List<Ghe> findByPhongChieuNative(@Param("idPC") String idPhongChieu);

    @Query("SELECT COUNT(g) FROM Ghe g WHERE g.phongChieu.id = :idPC")
    long countByPC(@Param("idPC") String idPC);

    boolean existsBySoGheAndPhongChieuId(String soGhe, String idPC);
}
