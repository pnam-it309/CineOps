package service.cinema.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.HoaDon;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, String>, JpaSpecificationExecutor<HoaDon> {
    // Kế thừa JpaSpecificationExecutor để sau này Đức có thể làm bộ lọc (filter)
    // tìm kiếm hóa đơn theo khoảng ngày, trạng thái giống như phần Vé nhé!
}
