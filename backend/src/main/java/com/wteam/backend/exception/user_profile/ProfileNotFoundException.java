package com.wteam.backend.exception.user_profile;

public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(String message) {
        super(message);
    }

    public ProfileNotFoundException(Long userId) {
        super("Profile with user "+ userId + " was not found");
    }
}
