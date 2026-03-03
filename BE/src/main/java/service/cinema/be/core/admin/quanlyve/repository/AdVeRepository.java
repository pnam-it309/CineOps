package service.cinema.be.core.admin.quanlyve.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.Ve;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AdVeRepository extends JpaRepository<Ve, String>, JpaSpecificationExecutor<Ve> {

    // ĐÃ SỬA: Đổi Long idSuatChieu và Long idGhe thành String
    boolean existsBySuatChieuIdAndGheIdAndTrangThai(String idSuatChieu, String idGhe, Integer trangThai);

    // Hàm phục vụ tìm kiếm có phân trang
    Page<Ve> findByMaVeContainingIgnoreCaseAndTrangThai(String maVe, Integer trangThai, Pageable pageable);

    // ĐÃ SỬA: Đổi Long idSuatChieu thành String
    List<Ve> findBySuatChieuIdAndTrangThai(String idSuatChieu, Integer trangThai);
    // THÊM CÁC HÀM NÀY:
    long countByTrangThai(Integer trangThai);

    @Query("SELECT SUM(v.giaThanhToan) FROM Ve v WHERE v.trangThai = 1")
    BigDecimal sumTotalRevenue();

}
