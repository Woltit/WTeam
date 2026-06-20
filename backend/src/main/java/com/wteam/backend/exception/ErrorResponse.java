package com.wteam.backend.exception;

import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

/**
 * The type Error response.
 */
public record ErrorResponse(
        int status,
        String message,
        String detailedMessage,
        Map<String, String> validationErrors,
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
        return new ErrorResponse(status.value(), message, null, null, Instant.now());
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
        return new ErrorResponse(status.value(), message, detailedMessage, null, Instant.now());
    }

    public static ErrorResponse ofValidationErrors(HttpStatus status, String message, Map<String, String> validationErrors) {
        return new ErrorResponse(status.value(), message, null, validationErrors, Instant.now());
    }

    public static ErrorResponse ofValidationErrors(HttpStatus status, String message, String detailedMessage, Map<String, String> validationErrors) {
        return new ErrorResponse(status.value(), message, detailedMessage, validationErrors, Instant.now());
    }
}
