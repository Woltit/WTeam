package com.wteam.backend.kafka.notification.dto;

import com.wteam.backend.common.enums.NotificationChannel;
import com.wteam.backend.common.enums.NotificationType;

import java.util.Map;

public record NotificationEvent(
        Long recipientUserId,
        NotificationType notificationType,
        NotificationChannel channel,
        Map<String, Object> payload
) {}
