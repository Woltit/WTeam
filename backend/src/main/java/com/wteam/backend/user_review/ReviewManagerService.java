package com.wteam.backend.user_review;

import com.wteam.backend.common.enums.ReviewStatus;
import com.wteam.backend.item_review.ItemReview;
import com.wteam.backend.item_review.ItemReviewRepository;
import com.wteam.backend.system_listeners.ReviewPublishedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReviewManagerService {
    private final ItemReviewRepository itemReviewRepository;
    private final UserReviewRepository userReviewRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CacheManager cacheManager;

    @org.springframework.beans.factory.annotation.Autowired
    @Lazy
    private ReviewManagerService self;

    /**
     * Checks if both parties (Renter and Owner) have left their reviews for the booking.
     * If so, publishes all pending reviews for this booking.
     */
    @Transactional
    public void checkAndPublishReviewsIfComplete(Long bookingId) {
        List<ItemReview> itemReviews = itemReviewRepository.findByBookingId(bookingId);
        List<UserReview> userReviews = userReviewRepository.findByBookingId(bookingId);

        boolean renterReviewedItem = !itemReviews.isEmpty();
        boolean ownerReviewedRenter = userReviews.stream().anyMatch(r -> r.getTargetRole() == com.wteam.backend.common.enums.TargetRole.RENTER);

        if (renterReviewedItem && ownerReviewedRenter) {
            self.publishAllForBooking(bookingId, itemReviews, userReviews);
        }
    }

    @Transactional
    public void publishAllForBooking(Long bookingId, List<ItemReview> itemReviews, List<UserReview> userReviews) {
        boolean publishedAny = false;

        for (ItemReview ir : itemReviews) {
            if (ir.getStatus() == ReviewStatus.PENDING) {
                ir.setStatus(ReviewStatus.PUBLISHED);
                itemReviewRepository.save(ir);
                publishedAny = true;

                Objects.requireNonNull(cacheManager.getCache("itemReviews"))
                        .evict(ir.getItem().getId());
            }
        }

        for (UserReview ur : userReviews) {
            if (ur.getStatus() == ReviewStatus.PENDING) {
                ur.setStatus(ReviewStatus.PUBLISHED);
                userReviewRepository.save(ur);
                publishedAny = true;

                Objects.requireNonNull(cacheManager.getCache("userReviews"))
                        .evict(ur.getTargetUser().getId());
            }
        }

        if (publishedAny) {
            eventPublisher.publishEvent(new ReviewPublishedEvent(this, bookingId));
        }
    }
}
