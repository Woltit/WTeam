package com.wteam.backend.exception.booking;

import com.wteam.backend.exception.base_with_status.ResourceNotFoundException;

public class BookingNotFoundException extends ResourceNotFoundException {
    public BookingNotFoundException(String message) {
        super(message);
    }

    public BookingNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookingNotFoundException(Long id) {
        super("Booking", id);
    }
}
