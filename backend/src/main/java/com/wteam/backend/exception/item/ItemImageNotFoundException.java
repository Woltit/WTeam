package com.wteam.backend.exception.item;

import com.wteam.backend.exception.base_with_status.ResourceNotFoundException;

public class ItemImageNotFoundException extends ResourceNotFoundException {
    public ItemImageNotFoundException(Long id) {
        super("Item image with id: " + id + " not found");
    }
}
