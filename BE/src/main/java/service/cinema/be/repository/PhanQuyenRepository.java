package service.cinema.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.PhanQuyen;

import java.util.Optional;

@Repository
public interface PhanQuyenRepository extends JpaRepository<PhanQuyen, String> {
    Optional<PhanQuyen> findByMaPhanQuyen(String maPhanQuyen);
}
