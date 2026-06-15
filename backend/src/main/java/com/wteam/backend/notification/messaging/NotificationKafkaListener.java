package com.wteam.backend.notification.messaging;

import com.wteam.backend.notification.NotificationService;
import com.wteam.backend.notification.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import static com.wteam.backend.kafka.KafkaConstants.NOTIFICATION_TOPIC_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationKafkaListener {
    private final NotificationService notificationService;

    @RetryableTopic(
            attempts = "3",
            backOff = @BackOff(delay = 2000, multiplier = 2.0),
            autoCreateTopics = "true"
    )
    @KafkaListener(topics = NOTIFICATION_TOPIC_NAME, groupId = "notification-group")
    public void processNotification(NotificationEvent event) {
        log.info("Received event: {}", event);
        notificationService.process(event);
    }

    @DltHandler
    public void handleDlt(NotificationEvent event, @Header(KafkaHeaders.EXCEPTION_MESSAGE) String errorMessage) {
        log.error("Dead Letter Topic: Не вдалося обробити подію {}. Помилка: {}", event, errorMessage);
    }
}
