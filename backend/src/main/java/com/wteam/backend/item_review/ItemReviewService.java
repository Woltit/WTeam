package com.wteam.backend.item_review;

import com.wteam.backend.booking.Booking;
import com.wteam.backend.booking.BookingRepository;
import com.wteam.backend.common.enums.BookingStatus;
import com.wteam.backend.common.enums.ReviewStatus;
import com.wteam.backend.common.enums.TargetRole;
import com.wteam.backend.exception.review.InvalidReviewStateException;
import com.wteam.backend.exception.review.ReviewAlreadyExistsException;
import com.wteam.backend.item_review.dto.ItemReviewRequest;
import com.wteam.backend.item_review.dto.ItemReviewResponse;
import com.wteam.backend.user_review.ReviewManagerService;
import com.wteam.backend.user_review.UserReview;
import com.wteam.backend.user_review.UserReviewRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    private final UserReviewRepository userReviewRepository;
    private final BookingRepository bookingRepository;
    private final ReviewManagerService reviewManagerService;
    private final ItemReviewMapper itemReviewMapper;

    @Transactional
    @CacheEvict(value = "itemReviews", key = "#result.itemId")
    public ItemReviewResponse submitItemReview(final Long bookingId, final Long reviewerId, final ItemReviewRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new InvalidReviewStateException("Reviews can only be left for COMPLETED bookings");
        }

        if (!booking.getRenter().getId().equals(reviewerId)) {
            throw new IllegalArgumentException("Only the renter can leave an item review for this booking");
        }

        if (itemReviewRepository.findByBookingIdAndReviewerId(bookingId, reviewerId).isPresent()) {
            throw new ReviewAlreadyExistsException("You have already left an item review for this booking");
        }

        ItemReview review = itemReviewMapper.toEntity(request);
        review.setItem(booking.getItem());
        review.setBooking(booking);
        review.setReviewer(booking.getRenter());
        review.setStatus(com.wteam.backend.common.enums.ReviewStatus.PENDING);

        review = itemReviewRepository.save(review);

        UserReview userReviewForOwner = getUserReview(request, booking);
        userReviewRepository.save(userReviewForOwner);

        reviewManagerService.checkAndPublishReviewsIfComplete(bookingId);

        return itemReviewMapper.toResponse(review);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "itemReviews", key = "#itemId")
    public List<ItemReviewResponse> getPublishedReviewsForItem(final Long itemId) {
        return new java.util.ArrayList<>(itemReviewRepository.findByItemIdAndStatusOrderByCreatedAtAsc(itemId, ReviewStatus.PUBLISHED).stream()
                .map(itemReviewMapper::toResponse)
                .toList());
    }

    private static @NonNull UserReview getUserReview(final ItemReviewRequest request, final Booking booking) {
        UserReview userReviewForOwner = new UserReview();
        userReviewForOwner.setTargetUser(booking.getItem().getOwner());
        userReviewForOwner.setReviewer(booking.getRenter());
        userReviewForOwner.setBooking(booking);
        userReviewForOwner.setTargetRole(TargetRole.OWNER);
        userReviewForOwner.setRating(request.getRating());
        userReviewForOwner.setComment(request.getComment());
        userReviewForOwner.setStatus(ReviewStatus.PENDING);
        return userReviewForOwner;
    }
}
