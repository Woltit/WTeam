package com.wteam.backend.exception.user;

/**
 * The type User already exists exception.
 */
public class UserAlreadyExistsException extends RuntimeException {
    /**
     * Instantiates a new User already exists exception.
     *
     * @param message the message
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }

    /**
     * Instantiates a new User already exists exception.
     *
     * @param userId the user id
     */
    public UserAlreadyExistsException(Long userId) {
        super("User with id " + userId + " already exists");
    }
}
