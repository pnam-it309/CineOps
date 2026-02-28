package service.cinema.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.PhimTheLoai;

@Repository
public interface PhimTheLoaiRepository extends JpaRepository<PhimTheLoai, String> {
    @Modifying
    @Query("DELETE FROM PhimTheLoai ptl WHERE ptl.phim.id = :idPhim")
    void deleteByPhimId(@Param("idPhim") String idPhim);
}
