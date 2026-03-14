package service.cinema.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.Ghe;

import java.util.List;

@Repository
public interface GheRepository extends JpaRepository<Ghe, String> {
    List<Ghe> findByPhongChieuIdOrderBySoGheAsc(String idPhongChieu);
}
