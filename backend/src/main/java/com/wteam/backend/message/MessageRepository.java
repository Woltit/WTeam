package com.wteam.backend.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторій для роботи з сутностями {@link Message}.
 * <p>
 * Забезпечує доступ до даних повідомлень у чатах у базі даних.
 * </p>
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}
