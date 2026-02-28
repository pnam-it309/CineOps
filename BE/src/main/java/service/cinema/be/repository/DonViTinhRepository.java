package service.cinema.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.DonViTinh;
@Repository
public interface DonViTinhRepository extends JpaRepository<DonViTinh, String> {
}