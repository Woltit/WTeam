package com.wteam.backend.system_listeners;

import org.springframework.context.ApplicationEvent;

public class ReviewPublishedEvent extends ApplicationEvent {
    private final Long bookingId;

    public ReviewPublishedEvent(Object source, Long bookingId) {
        super(source);
        this.bookingId = bookingId;
    }

    public Long getBookingId() {
        return bookingId;
    }
}
