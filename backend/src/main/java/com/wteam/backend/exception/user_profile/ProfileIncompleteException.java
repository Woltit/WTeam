package com.wteam.backend.exception.user_profile;

/**
 * The type Profile incomplete exception.
 */
public class ProfileIncompleteException extends RuntimeException {
    /**
     * Instantiates a new Profile incomplete exception.
     *
     * @param message the message
     */
    public ProfileIncompleteException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Profile incomplete exception.
     *
     * @param userId the user id
     */
    public ProfileIncompleteException(Long userId) {
        super("Profile for user with id " + userId + " is incomplete or not verified");
    }
}
