package com.wteam.backend.exception.user_profile;

public class ProfileIncompleteException extends RuntimeException {
    public ProfileIncompleteException(String message) {
        super(message);
    }

    public ProfileIncompleteException(Long userId) {
        super("Profile for user with id " + userId + " is incomplete or not verified");
    }
}
