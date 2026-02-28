package service.cinema.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.TheLoai;

@Repository
public interface TheLoaiRepository extends JpaRepository<TheLoai, String> {
}
