package com.wteam.backend.notification;

import com.wteam.backend.common.enums.NotificationChannel;
import com.wteam.backend.common.enums.NotificationType;
import com.wteam.backend.exception.notification.NotificationNotFoundException;
import com.wteam.backend.notification.channel.NotificationSender;
import com.wteam.backend.notification.dto.NotificationEvent;
import com.wteam.backend.notification.dto.NotificationResponse;
import com.wteam.backend.notification.template.NotificationMessage;
import com.wteam.backend.notification.template.NotificationMessageGenerator;
import com.wteam.backend.user.User;
import com.wteam.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationMessageGenerator messageGenerator;
    private final List<NotificationSender> senders;

    @Transactional
    public void process(final NotificationEvent event) {
        Long userId = event.recipientUserId();
        NotificationType type = event.notificationType();
        NotificationChannel channel = event.channel();
        Map<String, Object> payload = event.payload();

        User user = userRepository.getReferenceById(userId);

        NotificationMessage notificationMessage = messageGenerator.generate(type, payload);

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .channel(channel)
                .title(notificationMessage.title())
                .body(notificationMessage.body())
                .build();

        notificationRepository.save(notification);

        for (var sender : senders) {
            if (sender.supports(channel)) {
                sender.send(event);
            }
        }
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(notificationMapper::toResponse);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        int rowsAffected = notificationRepository.markAsRead(notificationId, userId);
        if (rowsAffected == 0) {
            throw new NotificationNotFoundException(notificationId);
        }
    }
}
