package service.cinema.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.KhungGio;

@Repository
public interface KhungGioRepository extends JpaRepository<KhungGio, String> {
}
