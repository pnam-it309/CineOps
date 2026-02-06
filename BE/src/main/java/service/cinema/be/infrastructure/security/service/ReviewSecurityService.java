package service.cinema.be.infrastructure.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import service.cinema.be.repository.ReviewRepository;

@Service("reviewSecurityService")
@RequiredArgsConstructor
public class ReviewSecurityService {

    private final ReviewRepository reviewRepository;

    /**
     * Check if the given user is the owner of the review
     *
     * @param reviewId The ID of the review to check
     * @param userId   The ID of the user to verify ownership
     * @return true if the user owns the review, false otherwise
     */
    public boolean isOwner(String reviewId, String userId) {
        if (reviewId == null || userId == null) {
            return false;
        }

        return reviewRepository.findByIdAndKhachHangId(reviewId, userId).isPresent();
    }
}
