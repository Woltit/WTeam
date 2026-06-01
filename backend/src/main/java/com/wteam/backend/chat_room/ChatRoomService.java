package com.wteam.backend.chat_room;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Сервіс для керування чат-кімнатами.
 * <p>
 * Обробляє бізнес-логіку, пов'язану зі створенням та отриманням чат-кімнат між користувачами.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
}
