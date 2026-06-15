package com.wteam.backend.item_review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторій для роботи з сутностями {@link ItemReview}.
 * <p>
 * Забезпечує доступ до даних про відгуки на товари у базі даних.
 * </p>
 */
@Repository
public interface ItemReviewRepository extends JpaRepository<ItemReview, Long> {
    java.util.Optional<ItemReview> findByBookingIdAndReviewerId(Long bookingId, Long reviewerId);
    java.util.List<ItemReview> findByBookingId(Long bookingId);
    java.util.List<ItemReview> findByStatusAndCreatedAtBefore(com.wteam.backend.common.enums.ReviewStatus status, java.time.Instant date);
    java.util.List<ItemReview> findByItemIdAndStatusOrderByCreatedAtAsc(Long itemId, com.wteam.backend.common.enums.ReviewStatus status);
}
