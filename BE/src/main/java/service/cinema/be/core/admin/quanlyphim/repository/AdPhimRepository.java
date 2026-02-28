package service.cinema.be.core.admin.quanlyphim.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.Phim;
import service.cinema.be.repository.PhimRepository;

@Repository
public interface AdPhimRepository extends PhimRepository{

    @Query("""
        SELECT DISTINCT p FROM Phim p
        LEFT JOIN p.phimTheLoais ptl
        LEFT JOIN ptl.theLoai tl
        WHERE (:tenPhim IS NULL OR LOWER(p.tenPhim) LIKE LOWER(CONCAT('%', :tenPhim, '%')))
          AND (:trangThai IS NULL OR p.trangThai = :trangThai)
          AND (:idTheLoai IS NULL OR tl.id = :idTheLoai)
        ORDER BY p.ngayKhoiChieu DESC
    """)
    Page<Phim> searchPhim(
            @Param("tenPhim") String tenPhim,
            @Param("trangThai") Integer trangThai,
            @Param("idTheLoai") String idTheLoai,
            Pageable pageable
    );

    boolean existsByTenPhimIgnoreCase(String tenPhim);
    boolean existsByTenPhimIgnoreCaseAndIdNot(String tenPhim, String id);
}

