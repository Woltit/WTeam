package com.wteam.backend.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Репозиторій для роботи з сутностями {@link Notification}.
 * <p>
 * Забезпечує доступ до даних сповіщень у базі даних.
 * </p>
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Modifying
    @Query(
            "UPDATE Notification n SET n.isRead = true " +
            "WHERE n.id = :notificationId " +
            "AND n.user.id = :userId AND n.isRead = false"
    )
    int markAsRead(@Param("notificationId") Long notificationId, @Param("userId") Long userId);
}
