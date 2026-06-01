package com.wteam.backend.exception.user;

import com.wteam.backend.exception.base_with_status.ResourceNotFoundException;

/**
 * The type User not found exception.
 */
public class UserNotFoundException extends ResourceNotFoundException {
    /**
     * Instantiates a new User not found exception.
     *
     * @param userId the user id
     */
    public UserNotFoundException(Long userId) {
        super("User with id " + userId + " was not found");
    }

    /**
     * Instantiates a new User not found exception.
     *
     * @param email the email
     */
    public UserNotFoundException(String email) {
        super("User with email " + email + " was not found");
    }
}
