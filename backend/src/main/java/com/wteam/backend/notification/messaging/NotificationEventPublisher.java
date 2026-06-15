package com.wteam.backend.notification.messaging;

import com.wteam.backend.exception.notification.NotificationPublishException;
import com.wteam.backend.notification.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.wteam.backend.config.ThreadPoolExecutorConfig.ASYNC_EXECUTOR_NAME;
import static com.wteam.backend.kafka.KafkaConstants.NOTIFICATION_TOPIC_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Async(ASYNC_EXECUTOR_NAME)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishToKafka(NotificationEvent event) {
        log.info("Publishing event: {}", event);
        try {
            kafkaTemplate.send(NOTIFICATION_TOPIC_NAME, event).get();
            log.debug("Event successfully sent to Kafka");
        } catch (Exception e) {
            throw new NotificationPublishException("Failed to send notification event to Kafka", e);
        }
    }
}
