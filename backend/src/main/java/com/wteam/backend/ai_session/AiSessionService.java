package com.wteam.backend.ai_session;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Сервіс для керування сесіями взаємодії з ШІ.
 * <p>
 * Обробляє бізнес-логіку, пов'язану зі збереженням та отриманням історії запитів до ШІ.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AiSessionService {
    private final AiSessionRepository aiSessionRepository;
}
