package service.cinema.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.LichSuHoaDon;

@Repository
public interface LichSuHoaDonRepository extends JpaRepository<LichSuHoaDon, String> {
}