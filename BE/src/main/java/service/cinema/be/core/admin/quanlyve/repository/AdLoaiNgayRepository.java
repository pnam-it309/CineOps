package service.cinema.be.core.admin.quanlyve.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.LoaiNgay;
import java.util.List;

@Repository
public interface AdLoaiNgayRepository extends JpaRepository<LoaiNgay, String> {
}