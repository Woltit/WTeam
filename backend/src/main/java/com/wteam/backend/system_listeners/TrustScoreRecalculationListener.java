package com.wteam.backend.system_listeners;

import com.wteam.backend.booking.Booking;
import com.wteam.backend.booking.BookingRepository;
import com.wteam.backend.common.enums.ReviewStatus;
import com.wteam.backend.common.enums.TargetRole;
import com.wteam.backend.item.Item;
import com.wteam.backend.item.ItemRepository;
import com.wteam.backend.item_review.ItemReview;
import com.wteam.backend.item_review.ItemReviewRepository;
import com.wteam.backend.user_profile.UserProfileRepository;
import com.wteam.backend.user_review.UserReview;
import com.wteam.backend.user_review.UserReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrustScoreRecalculationListener {
    private final BookingRepository bookingRepository;
    private final ItemReviewRepository itemReviewRepository;
    private final UserReviewRepository userReviewRepository;
    private final UserProfileRepository userProfileRepository;
    private final ItemRepository itemRepository;

    @Async
    @EventListener
    @Transactional
    public void handleReviewPublishedEvent(ReviewPublishedEvent event) {
        log.info("Recalculating scores for booking id: {}", event.getBookingId());

        Booking booking = bookingRepository.findById(event.getBookingId()).orElse(null);
        if (booking == null) return;

        // Recalculate Item Rating
        recalculateItemRating(booking.getItem());

        // Recalculate User Scores
        recalculateUserTrustScore(booking.getRenter().getId(), TargetRole.RENTER);
        recalculateUserTrustScore(booking.getItem().getOwner().getId(), TargetRole.OWNER);
    }

    private void recalculateItemRating(Item item) {
        List<ItemReview> allReviews = itemReviewRepository.findByItemIdAndStatusOrderByCreatedAtAsc(item.getId(), ReviewStatus.PUBLISHED);

        if (allReviews.isEmpty()) return;

        double totalWeight = 0;
        double weightedSum = 0;
        
        for (int i = 0; i < allReviews.size(); i++) {
            ItemReview review = allReviews.get(i);
            double weight = 1.0 + (i * 0.1); 
            totalWeight += weight;
            weightedSum += (review.getRating() * weight);
        }

        if (totalWeight == 0.0) {
            throw new IllegalStateException("Total weight cannot be 0");
        }

        double newRating = weightedSum / totalWeight;
        item.setRating(BigDecimal.valueOf(newRating).setScale(2, RoundingMode.HALF_UP));
        item.setTotalReviews(allReviews.size());
        itemRepository.save(item);
    }

    private void recalculateUserTrustScore(Long userId, TargetRole targetRole) {
        List<UserReview> userReviews = userReviewRepository.findByTargetUserIdAndTargetRoleAndStatusOrderByCreatedAtAsc(userId, targetRole, ReviewStatus.PUBLISHED);

        if (userReviews.isEmpty()) return;

        double totalWeight = 0;
        double weightedSum = 0;

        for (int i = 0; i < userReviews.size(); i++) {
            UserReview review = userReviews.get(i);
            double weight = 1.0 + (i * 0.1);
            totalWeight += weight;
            weightedSum += (review.getRating() * weight);
        }

        if (totalWeight == 0.0) {
            throw new IllegalStateException("Total weight cannot be 0");
        }

        double newScore = weightedSum / totalWeight;

        userProfileRepository.findByUserId(userId).ifPresent(profile -> {
            if (targetRole == TargetRole.RENTER) {
                profile.setRenterTrustScore(BigDecimal.valueOf(newScore).setScale(2, RoundingMode.HALF_UP));
            } else {
                profile.setOwnerTrustScore(BigDecimal.valueOf(newScore).setScale(2, RoundingMode.HALF_UP));
            }
            userProfileRepository.save(profile);
        });
    }
}
