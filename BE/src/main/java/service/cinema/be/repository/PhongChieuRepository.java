package service.cinema.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.PhongChieu;

@Repository
public interface PhongChieuRepository extends JpaRepository<PhongChieu, String> {
}
