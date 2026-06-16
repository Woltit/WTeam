package com.wteam.backend.exception.booking;

import com.wteam.backend.exception.base_with_status.BadRequestException;

public class InvalidBookingStateException extends BadRequestException {
    public InvalidBookingStateException(String message) {
        super(message);
    }
}
