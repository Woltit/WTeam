package com.wteam.backend.exception.user_profile;


import com.wteam.backend.exception.base_with_status.ResourceNotFoundException;

/**
 * The type Profile not found exception.
 */
public class ProfileNotFoundException extends ResourceNotFoundException {
    /**
     * Instantiates a new Profile not found exception.
     *
     * @param message the message
     */
    public ProfileNotFoundException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Profile not found exception.
     *
     * @param userId the user id
     */
    public ProfileNotFoundException(Long userId) {
        super("Profile with user "+ userId + " was not found");
    }
}
