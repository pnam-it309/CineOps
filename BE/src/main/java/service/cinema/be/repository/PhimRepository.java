package service.cinema.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.Phim;

import java.util.List;

@Repository
public interface PhimRepository extends JpaRepository<Phim, String> {

    @Query("SELECT p FROM Phim p WHERE p.trangThai = 1 ORDER BY p.ngayKhoiChieu DESC")
    List<Phim> findAllActive();
}
