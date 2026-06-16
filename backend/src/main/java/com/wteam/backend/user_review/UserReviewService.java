package com.wteam.backend.user_review;

import com.wteam.backend.booking.Booking;
import com.wteam.backend.booking.BookingRepository;
import com.wteam.backend.common.enums.BookingStatus;
import com.wteam.backend.common.enums.ReviewStatus;
import com.wteam.backend.common.enums.TargetRole;
import com.wteam.backend.user.User;
import com.wteam.backend.user.UserRepository;
import com.wteam.backend.exception.review.InvalidReviewStateException;
import com.wteam.backend.exception.review.ReviewAlreadyExistsException;
import com.wteam.backend.user_review.dto.UserReviewRequest;
import com.wteam.backend.user_review.dto.UserReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserReviewService {

    private final UserReviewRepository userReviewRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ReviewManagerService reviewManagerService;

    @Transactional
    public UserReviewResponse submitUserReview(Long bookingId, Long reviewerId, UserReviewRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new InvalidReviewStateException("Reviews can only be left for COMPLETED bookings");
        }

        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new IllegalArgumentException("Reviewer not found"));

        TargetRole targetRole;
        User targetUser;

        if (booking.getRenter().getId().equals(reviewerId)) {
            targetRole = TargetRole.OWNER;
            targetUser = booking.getItem().getOwner();
        } else if (booking.getItem().getOwner().getId().equals(reviewerId)) {
            targetRole = TargetRole.RENTER;
            targetUser = booking.getRenter();
        } else {
            throw new IllegalArgumentException("User is not part of this booking");
        }

        if (userReviewRepository.findByBookingIdAndReviewerIdAndTargetUserId(bookingId, reviewerId, targetUser.getId()).isPresent()) {
            throw new ReviewAlreadyExistsException("Review already exists");
        }

        UserReview review = new UserReview();
        review.setTargetUser(targetUser);
        review.setReviewer(reviewer);
        review.setBooking(booking);
        review.setTargetRole(targetRole);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setStatus(ReviewStatus.PENDING);

        review = userReviewRepository.save(review);

        reviewManagerService.checkAndPublishReviewsIfComplete(bookingId);

        return UserReviewResponse.builder()
                .id(review.getId())
                .targetUserId(review.getTargetUser().getId())
                .reviewerId(review.getReviewer().getId())
                .bookingId(review.getBooking().getId())
                .targetRole(review.getTargetRole())
                .rating(review.getRating())
                .comment(review.getComment())
                .status(review.getStatus())
                .createdAt(review.getCreatedAt())
                .build();
    }
    @Transactional(readOnly = true)
    public java.util.List<UserReviewResponse> getPublishedReviewsForUser(Long userId) {
        return userReviewRepository.findAll().stream() // Ideally we should use a proper custom query, but we can do stream for simplicity or add method to repo
                .filter(r -> r.getTargetUser().getId().equals(userId))
                .filter(r -> r.getStatus() == ReviewStatus.PUBLISHED)
                .map(review -> UserReviewResponse.builder()
                        .id(review.getId())
                        .targetUserId(review.getTargetUser().getId())
                        .reviewerId(review.getReviewer().getId())
                        .bookingId(review.getBooking().getId())
                        .targetRole(review.getTargetRole())
                        .rating(review.getRating())
                        .comment(review.getComment())
                        .status(review.getStatus())
                        .createdAt(review.getCreatedAt())
                        .build())
                .toList();
    }
}
