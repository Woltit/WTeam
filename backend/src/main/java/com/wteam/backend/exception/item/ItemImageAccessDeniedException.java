package com.wteam.backend.exception.item;

import com.wteam.backend.exception.base_with_status.ForbiddenOperationException;

public class ItemImageAccessDeniedException extends ForbiddenOperationException {
    public ItemImageAccessDeniedException(String message) {
        super(message);
    }
}
