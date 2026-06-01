package com.wteam.backend.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Сервіс для керування повідомленнями в чатах.
 * <p>
 * Обробляє бізнес-логіку, пов'язану з надсиланням, отриманням та зберіганням повідомлень.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
}
