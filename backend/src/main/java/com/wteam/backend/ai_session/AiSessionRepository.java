package com.wteam.backend.ai_session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторій для роботи з сутностями {@link AiSession}.
 * <p>
 * Забезпечує доступ до даних сесій взаємодії з ШІ у базі даних.
 * </p>
 */
@Repository
public interface AiSessionRepository extends JpaRepository<AiSession, Long> {
}
