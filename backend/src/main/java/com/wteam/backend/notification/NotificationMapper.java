package com.wteam.backend.notification;

import com.wteam.backend.common.interfaces.Mapper;
import com.wteam.backend.notification.dto.NotificationResponse;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper implements Mapper<Void, NotificationResponse, Notification> {

    @Override
    public NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getBody(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }

    @Override
    public Notification toEntity(Void dto) {
        return null;
    }
}
