package com.wteam.backend.exception.review;

import com.wteam.backend.exception.base_with_status.BadRequestException;

public class InvalidReviewStateException extends BadRequestException {
    public InvalidReviewStateException(String message) {
        super(message);
    }
}
