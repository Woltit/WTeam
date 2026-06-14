package com.wteam.backend.kafka.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторій для роботи з сутностями {@link Notification}.
 * <p>
 * Забезпечує доступ до даних сповіщень у базі даних.
 * </p>
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
