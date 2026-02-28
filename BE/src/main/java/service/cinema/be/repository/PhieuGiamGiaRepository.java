package service.cinema.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.PhieuGiamGia;

@Repository
public interface PhieuGiamGiaRepository
        extends JpaRepository<PhieuGiamGia, String> {
}