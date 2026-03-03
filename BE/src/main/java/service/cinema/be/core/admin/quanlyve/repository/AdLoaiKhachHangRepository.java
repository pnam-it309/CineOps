package service.cinema.be.core.admin.quanlyve.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.LoaiKhachHang;
import java.util.List;

@Repository
public interface AdLoaiKhachHangRepository extends JpaRepository<LoaiKhachHang, String> {

    // Chỉ lấy ra các loại khách hàng đang hoạt động (trạng thái = 1) để đổ lên Vue 3
    List<LoaiKhachHang> findByTrangThai(Integer trangThai);
}
