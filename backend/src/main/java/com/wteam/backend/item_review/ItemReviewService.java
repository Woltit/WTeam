package com.wteam.backend.item_review;

import com.wteam.backend.item_review.dto.ItemReviewRequest;
import com.wteam.backend.item_review.dto.ItemReviewResponse;
import com.wteam.backend.exception.review.InvalidReviewStateException;
import com.wteam.backend.exception.review.ReviewAlreadyExistsException;
import com.wteam.backend.common.enums.BookingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Сервіс для керування відгуками на товари.
 * <p>
 * Обробляє бізнес-логіку, пов'язану зі створенням, редагуванням та отриманням відгуків на товари.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ItemReviewService {
    private final ItemReviewRepository itemReviewRepository;
    private final com.wteam.backend.user_review.UserReviewRepository userReviewRepository;
    private final com.wteam.backend.booking.BookingRepository bookingRepository;
    private final com.wteam.backend.user_review.ReviewManagerService reviewManagerService;

    @org.springframework.transaction.annotation.Transactional
    public ItemReviewResponse submitItemReview(Long bookingId, Long reviewerId, ItemReviewRequest request) {
        com.wteam.backend.booking.Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new InvalidReviewStateException("Reviews can only be left for COMPLETED bookings");
        }

        if (!booking.getRenter().getId().equals(reviewerId)) {
            throw new IllegalArgumentException("Only the renter can leave an item review for this booking");
        }

        if (itemReviewRepository.findByBookingIdAndReviewerId(bookingId, reviewerId).isPresent()) {
            throw new ReviewAlreadyExistsException("Review already exists");
        }

        ItemReview review = new ItemReview();
        review.setItem(booking.getItem());
        review.setBooking(booking);
        review.setReviewer(booking.getRenter());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setStatus(com.wteam.backend.common.enums.ReviewStatus.PENDING);

        review = itemReviewRepository.save(review);

        // Duplicate the review for the Owner so the Owner gets a UserReview (targetRole = OWNER)
        com.wteam.backend.user_review.UserReview userReviewForOwner = new com.wteam.backend.user_review.UserReview();
        userReviewForOwner.setTargetUser(booking.getItem().getOwner());
        userReviewForOwner.setReviewer(booking.getRenter());
        userReviewForOwner.setBooking(booking);
        userReviewForOwner.setTargetRole(com.wteam.backend.common.enums.TargetRole.OWNER);
        userReviewForOwner.setRating(request.getRating());
        userReviewForOwner.setComment(request.getComment());
        userReviewForOwner.setStatus(com.wteam.backend.common.enums.ReviewStatus.PENDING);
        userReviewRepository.save(userReviewForOwner);

        reviewManagerService.checkAndPublishReviewsIfComplete(bookingId);

        return com.wteam.backend.item_review.dto.ItemReviewResponse.builder()
                .id(review.getId())
                .itemId(review.getItem().getId())
                .reviewerId(review.getReviewer().getId())
                .bookingId(review.getBooking().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .status(review.getStatus())
                .createdAt(review.getCreatedAt())
                .build();
    }
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public java.util.List<com.wteam.backend.item_review.dto.ItemReviewResponse> getPublishedReviewsForItem(Long itemId) {
        return itemReviewRepository.findByItemIdAndStatusOrderByCreatedAtAsc(itemId, com.wteam.backend.common.enums.ReviewStatus.PUBLISHED).stream()
                .map(review -> com.wteam.backend.item_review.dto.ItemReviewResponse.builder()
                        .id(review.getId())
                        .itemId(review.getItem().getId())
                        .reviewerId(review.getReviewer().getId())
                        .bookingId(review.getBooking().getId())
                        .rating(review.getRating())
                        .comment(review.getComment())
                        .status(review.getStatus())
                        .createdAt(review.getCreatedAt())
                        .build())
                .toList();
    }
}
