package service.cinema.be.core.admin.quanlyhoadon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.LichSuHoaDon;

import java.util.List;

@Repository
public interface AdLichSuHoaDonRepository extends JpaRepository<LichSuHoaDon, String> {

    // Lấy lịch sử thao tác, sắp xếp thời gian giảm dần (mới nhất lên đầu)
    List<LichSuHoaDon> findByHoaDonIdOrderByThoiGianHanhDongDesc(String idHoaDon);
}