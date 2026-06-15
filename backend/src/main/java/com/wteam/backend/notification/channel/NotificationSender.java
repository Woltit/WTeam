package com.wteam.backend.notification.channel;

import com.wteam.backend.common.enums.NotificationChannel;
import com.wteam.backend.notification.dto.NotificationEvent;

public interface NotificationSender {
    void send(NotificationEvent event);
    boolean supports(NotificationChannel channel);
}
