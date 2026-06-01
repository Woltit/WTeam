package com.wteam.backend.exception.item;

import com.wteam.backend.exception.base_with_status.ResourceNotFoundException;

public class ItemNotFoundException extends ResourceNotFoundException {
    public ItemNotFoundException(String message) {
        super(message);
    }

    public ItemNotFoundException(Long id) {
        super("Item with id: " + id + " not found");
    }
}
