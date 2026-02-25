package service.cinema.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.LoaiGhe;

@Repository
public interface LoaiGheRepository extends JpaRepository<LoaiGhe, String> {
    boolean existsByTenLoai(String tenLoai);
}
