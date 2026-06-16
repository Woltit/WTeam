package com.wteam.backend.exception.cloudinary;

import com.wteam.backend.exception.base_with_status.InternalServerErrorException;

public class ImageUploadException extends InternalServerErrorException {
    public ImageUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
