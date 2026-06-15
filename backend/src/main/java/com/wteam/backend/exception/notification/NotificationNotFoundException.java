package com.wteam.backend.exception.notification;

import com.wteam.backend.exception.base_with_status.ResourceNotFoundException;

public class NotificationNotFoundException extends ResourceNotFoundException {
    public NotificationNotFoundException(String message) {
        super(message);
    }

    public NotificationNotFoundException(Long id) {
        super("Notification not found with id: " + id);
    }
}
