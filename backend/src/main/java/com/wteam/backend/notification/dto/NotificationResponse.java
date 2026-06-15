package com.wteam.backend.notification.dto;

import com.wteam.backend.common.enums.NotificationType;

import java.time.Instant;

public record NotificationResponse(
        Long id,
        NotificationType type,
        String title,
        String body,
        boolean isRead,
        Instant createdAt
) {}
