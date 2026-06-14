package com.wteam.backend.kafka.notification;

import com.wteam.backend.booking.dto.BookingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.wteam.backend.kafka.topics.KafkaConstants.NOTIFICATION_TOPIC_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    @KafkaListener(topics = NOTIFICATION_TOPIC_NAME, groupId = "notificaiton-group")
    public void handleBookingEvent(BookingResponse bookingResponse) {
        log.info("Received booking event: {}", bookingResponse);


    }
}
