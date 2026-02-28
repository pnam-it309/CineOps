package service.cinema.be.core.admin.QuanLyPhieuGiamGia.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.PhieuGiamGia;

import java.time.LocalDateTime;

@Repository
public interface AdPhieuGiamGiaRepository extends JpaRepository<PhieuGiamGia, String> {

boolean existsByMaPhieuGiamGia(String maPhieuGiamGia);

    @Query("""
    SELECT p FROM PhieuGiamGia p
    WHERE (:keyword IS NULL OR LOWER(p.maPhieuGiamGia) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(p.tenPhieu) LIKE LOWER(CONCAT('%', :keyword, '%')))
    AND (:trangThai IS NULL OR p.trangThai = :trangThai)
    """)
    Page<PhieuGiamGia> search(
            @Param("keyword") String keyword,
            @Param("trangThai") Integer trangThai,
            Pageable pageable
    );
}