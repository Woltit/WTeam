package com.wteam.backend.kafka.listener;

import com.wteam.backend.kafka.notification.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.wteam.backend.kafka.topics.KafkaConstants.NOTIFICATION_TOPIC_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishToKafka(NotificationEvent event) {
        log.info("Publishing event: {}", event);

        kafkaTemplate.send(NOTIFICATION_TOPIC_NAME, event);
    }
}
