package service.cinema.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import service.cinema.be.entity.HoaDon;

import java.util.List;
import java.util.Optional;

/**
 * Repository for HoaDon (Invoice/Booking) entity
 */
@Repository
public interface BookingRepository extends JpaRepository<HoaDon, String> {

    /**
     * Find all invoices by customer ID
     *
     * @param khachHangId The customer ID
     * @return List of HoaDon for the customer
     */
    @Query("SELECT h FROM HoaDon h WHERE h.khachHang.id = :khachHangId")
    List<HoaDon> findByKhachHangId(@Param("khachHangId") String khachHangId);

    /**
     * Find invoice by ID and customer ID (for ownership verification)
     *
     * @param id           Invoice ID
     * @param khachHangId  Customer ID
     * @return Optional of HoaDon if found and owned by customer
     */
    @Query("SELECT h FROM HoaDon h WHERE h.id = :id AND h.khachHang.id = :khachHangId")
    Optional<HoaDon> findByIdAndKhachHangId(@Param("id") String id, @Param("khachHangId") String khachHangId);

}
