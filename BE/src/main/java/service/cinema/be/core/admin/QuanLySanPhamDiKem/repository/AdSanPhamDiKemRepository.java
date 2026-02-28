package service.cinema.be.core.admin.QuanLySanPhamDiKem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import service.cinema.be.entity.SanPhamDichVu;

public interface AdSanPhamDiKemRepository extends JpaRepository<SanPhamDichVu, String> {

    boolean existsByTenSanPhamIgnoreCase(String tenSanPham);

    @Query("""
        SELECT sp FROM SanPhamDichVu sp
        WHERE (:keyword IS NULL OR LOWER(sp.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:trangThai IS NULL OR sp.trangThai = :trangThai)
    """)
    Page<SanPhamDichVu> getAll(
            Pageable pageable,
            @Param("keyword") String keyword,
            @Param("trangThai") Integer trangThai
    );
}