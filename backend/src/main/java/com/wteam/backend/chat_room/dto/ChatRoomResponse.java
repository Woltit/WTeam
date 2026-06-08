package com.wteam.backend.chat_room.dto;

import java.time.Instant;

public record ChatRoomResponse(
        Long id,
        Long bookingId,
        String itemTitle,
        Long otherUserId,
        String otherUserName,
        Instant createdAt
) {}
