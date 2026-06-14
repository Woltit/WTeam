package com.wteam.backend.exception.booking;

import com.wteam.backend.exception.base_with_status.ResourceAlreadyExistsException;

import java.time.LocalDate;

public class ItemNotAvailableException extends ResourceAlreadyExistsException {
    public ItemNotAvailableException(Long itemId, LocalDate start, LocalDate end) {
        super(String.format("Item with ID %d is not available from %s to %s", itemId, start, end));
    }
}
