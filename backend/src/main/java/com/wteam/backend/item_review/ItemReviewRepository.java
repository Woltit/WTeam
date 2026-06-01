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
}
