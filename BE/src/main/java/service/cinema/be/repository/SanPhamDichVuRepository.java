package service.cinema.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.SanPhamDichVu;

@Repository
public interface SanPhamDichVuRepository extends JpaRepository<SanPhamDichVu, String> {
}
