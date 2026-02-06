package service.cinema.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.DanhGiaPhim;

import java.util.List;
import java.util.Optional;

/**
 * Repository for DanhGiaPhim (Movie Review) entity
 */
@Repository
public interface ReviewRepository extends JpaRepository<DanhGiaPhim, String> {

    /**
     * Find all reviews by movie ID
     *
     * @param phimId The movie ID
     * @return List of reviews for the movie
     */
    @Query("SELECT d FROM DanhGiaPhim d WHERE d.phim.id = :phimId")
    List<DanhGiaPhim> findByPhimId(@Param("phimId") String phimId);

    /**
     * Find all reviews by customer ID
     *
     * @param khachHangId The customer ID
     * @return List of reviews by the customer
     */
    @Query("SELECT d FROM DanhGiaPhim d WHERE d.khachHang.id = :khachHangId")
    List<DanhGiaPhim> findByKhachHangId(@Param("khachHangId") String khachHangId);

    /**
     * Find review by ID and customer ID (for ownership verification)
     *
     * @param id           Review ID
     * @param khachHangId  Customer ID
     * @return Optional of DanhGiaPhim if found and owned by customer
     */
    @Query("SELECT d FROM DanhGiaPhim d WHERE d.id = :id AND d.khachHang.id = :khachHangId")
    Optional<DanhGiaPhim> findByIdAndKhachHangId(@Param("id") String id, @Param("khachHangId") String khachHangId);

}
