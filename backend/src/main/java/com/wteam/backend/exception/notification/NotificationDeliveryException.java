package com.wteam.backend.exception.notification;

import com.wteam.backend.exception.base_with_status.InternalServerErrorException;

public class NotificationDeliveryException extends InternalServerErrorException {
    public NotificationDeliveryException(String message) {
        super(message);
    }

    public NotificationDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
