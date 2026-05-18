package com.wteam.backend.exception.user;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public UserAlreadyExistsException(Long userId) {
        super("User with id " + userId + " already exists");
    }
}
