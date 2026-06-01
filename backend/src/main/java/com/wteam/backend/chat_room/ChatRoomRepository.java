package com.wteam.backend.chat_room;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторій для роботи з сутностями {@link ChatRoom}.
 * <p>
 * Забезпечує доступ до даних чат-кімнат у базі даних.
 * </p>
 */
@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
