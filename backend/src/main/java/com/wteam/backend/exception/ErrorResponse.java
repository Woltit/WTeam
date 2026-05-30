package com.wteam.backend.exception;

import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * The type Error response.
 */
public record ErrorResponse(
        int status,
        String message,
        String detailedMessage,
        Instant timestamp
) {
    /**
     * Of error response.
     *
     * @param status  the status
     * @param message the message
     * @return the error response
     */
    public static ErrorResponse of(HttpStatus status, String message) {
        return new ErrorResponse(status.value(), message, null, Instant.now());
    }

    /**
     * Of error response.
     *
     * @param status          the status
     * @param message         the message
     * @param detailedMessage the detailed message
     * @return the error response
     */
    public static ErrorResponse of(HttpStatus status, String message, String detailedMessage) {
        return new ErrorResponse(status.value(), message, detailedMessage, Instant.now());
    }
}
