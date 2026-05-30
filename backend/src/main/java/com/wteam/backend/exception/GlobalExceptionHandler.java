package com.wteam.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * The type Global exception handler.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle bad credentials exception response entity.
     *
     * @param e the e
     * @return the response entity
     */
// 401
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
        return buildResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    /**
     * Handle username not found exception response entity.
     *
     * @param e the e
     * @return the response entity
     */
// 401
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return buildResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    /**
     * Handle illegal argument exception response entity.
     *
     * @param e the e
     * @return the response entity
     */
// 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }


    /**
     * Handle illegal state exception response entity.
     *
     * @param e the e
     * @return the response entity
     */
// 409
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
        return buildResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    /**
     * Handle exception response entity.
     *
     * @param e the e
     * @return the response entity
     */
// 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }


    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(ErrorResponse.of(status, message));
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, String detailedMessage) {
        return ResponseEntity
                .status(status)
                .body(ErrorResponse.of(status, message, detailedMessage));
    }
}
