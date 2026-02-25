package service.cinema.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.SuatChieu;

@Repository
public interface SuatChieuRepository extends JpaRepository<SuatChieu, String> {
}
