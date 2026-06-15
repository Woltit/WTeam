package com.wteam.backend.kafka.topics;

public final class KafkaConstants {
    private KafkaConstants() {
        throw new UnsupportedOperationException();
    }

    public static final String NOTIFICATION_TOPIC_NAME = "notification.events";
}
