package service.cinema.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.ChiTietSanPhamDiKem;

@Repository
public interface ChiTietSanPhamDiKemRepository extends JpaRepository<ChiTietSanPhamDiKem, String> {
    // Xóa tất cả chi tiết của một sản phẩm (dùng khi update)
    void deleteBySanPhamId(String sanPhamId);
}