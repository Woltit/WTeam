package com.wteam.backend.exception;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public record ErrorResponse(
        int status,
        String message,
        String detailedMessage,
        Instant timestamp
) {
    public static ErrorResponse of(HttpStatus status, String message) {
        return new ErrorResponse(status.value(), message, null, Instant.now());
    }

    public static ErrorResponse of(HttpStatus status, String message, String detailedMessage) {
        return new ErrorResponse(status.value(), message, detailedMessage, Instant.now());
    }
}
