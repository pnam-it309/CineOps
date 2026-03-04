package service.cinema.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.ChiTietSanPhamDiKem;

@Repository
public interface ChiTietSanPhamDiKemRepository extends JpaRepository<ChiTietSanPhamDiKem, String> {

    // Xóa tất cả chi tiết của một sản phẩm (dùng khi update)
    void deleteBySanPhamId(String sanPhamId);

    /**
     * Trừ kho atomic — trả về số rows bị ảnh hưởng.
     * Nếu trả về 0 nghĩa là không đủ hàng (race-condition safe).
     * WHERE so_luong_ton >= :soLuong đảm bảo không cho kho về âm.
     */
    @Modifying
    @Query("UPDATE ChiTietSanPhamDiKem c SET c.soLuongTon = c.soLuongTon - :soLuong " +
           "WHERE c.id = :id AND c.soLuongTon >= :soLuong")
    int decrementStock(@Param("id") String id, @Param("soLuong") int soLuong);
}