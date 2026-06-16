package com.wteam.backend.exception.review;

import com.wteam.backend.exception.base_with_status.ResourceAlreadyExistsException;

public class ReviewAlreadyExistsException extends ResourceAlreadyExistsException {
    public ReviewAlreadyExistsException(String message) {
        super(message);
    }
}
