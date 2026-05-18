package com.wteam.backend.exception.user;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long userId) {
        super("User with id " + userId + " was not found");
    }

    public UserNotFoundException(String email) {
        super("User with email " + email + " was not found");
    }
}
