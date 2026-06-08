package com.wteam.backend.message.dto;

import java.time.Instant;

public record MessageResponse(
        Long id,
        Long senderId,
        String senderName,
        String messageText,
        boolean isRead,
        Instant createdAt
) {}
