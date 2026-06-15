package com.wteam.backend.system_listeners;

import com.wteam.backend.common.enums.ReviewStatus;
import com.wteam.backend.item_review.ItemReview;
import com.wteam.backend.item_review.ItemReviewRepository;
import com.wteam.backend.user_review.UserReview;
import com.wteam.backend.user_review.UserReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewPublishScheduler {

    private final ItemReviewRepository itemReviewRepository;
    private final UserReviewRepository userReviewRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${rentgo.reviews.blind-period-minutes:4320}")
    private long blindPeriodMinutes;

    @Scheduled(cron = "0 0 2 * * ?") // Runs every day at 2 AM
    @Transactional
    public void autoPublishOldReviews() {
        Instant cutoffTime = Instant.now().minus(blindPeriodMinutes, ChronoUnit.MINUTES);
        log.info("Running auto-publish scheduler for reviews created before: {}", cutoffTime);

        List<ItemReview> pendingItemReviews = itemReviewRepository.findByStatusAndCreatedAtBefore(ReviewStatus.PENDING, cutoffTime);
        List<UserReview> pendingUserReviews = userReviewRepository.findByStatusAndCreatedAtBefore(ReviewStatus.PENDING, cutoffTime);

        boolean anyPublished = false;
        Set<Long> updatedBookingIds = pendingItemReviews.stream()
                .map(r -> r.getBooking().getId())
                .collect(Collectors.toSet());
        updatedBookingIds.addAll(pendingUserReviews.stream()
                .map(r -> r.getBooking().getId())
                .collect(Collectors.toSet()));

        for (ItemReview ir : pendingItemReviews) {
            ir.setStatus(ReviewStatus.PUBLISHED);
            itemReviewRepository.save(ir);
            anyPublished = true;
        }

        for (UserReview ur : pendingUserReviews) {
            ur.setStatus(ReviewStatus.PUBLISHED);
            userReviewRepository.save(ur);
            anyPublished = true;
        }

        if (anyPublished) {
            log.info("Auto-published reviews for {} bookings", updatedBookingIds.size());
            for (Long bookingId : updatedBookingIds) {
                eventPublisher.publishEvent(new ReviewPublishedEvent(this, bookingId));
            }
        }
    }
}
