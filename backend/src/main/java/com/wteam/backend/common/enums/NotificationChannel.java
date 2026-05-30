package com.wteam.backend.common.enums;

import lombok.Getter;

/**
 * The enum Notification channel.
 */
@Getter
public enum NotificationChannel {
    /**
     * Email notification channel.
     */
    EMAIL,
    /**
     * Push notification channel.
     */
    PUSH,
    /**
     * In app notification channel.
     */
    IN_APP
}
