package service.cinema.be.infrastructure.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.cinema.be.repository.BookingRepository;

@Service("bookingSecurityService")
@RequiredArgsConstructor
public class BookingSecurityService {

    private final BookingRepository bookingRepository;

    /**
     * Check if the given user is the owner of the booking
     *
     * @param bookingId The ID of the booking to check
     * @param userId    The ID of the user to verify ownership
     * @return true if the user owns the booking, false otherwise
     */
    public boolean isOwner(String bookingId, String userId) {
        if (bookingId == null || userId == null) {
            return false;
        }

        return bookingRepository.findByIdAndKhachHangId(bookingId, userId).isPresent();
    }
}
