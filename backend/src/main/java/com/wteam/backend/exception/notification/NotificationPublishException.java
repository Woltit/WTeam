package com.wteam.backend.exception.notification;

import com.wteam.backend.exception.base_with_status.InternalServerErrorException;

public class NotificationPublishException extends InternalServerErrorException {
    public NotificationPublishException(String message, Throwable cause) {
        super(message, cause);
    }
}
