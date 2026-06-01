package com.wteam.backend.exception.category;

import com.wteam.backend.exception.base_with_status.ResourceNotFoundException;

public class CategoryNotFoundException extends ResourceNotFoundException {
    public CategoryNotFoundException(String message) {
        super(message);
    }

    public CategoryNotFoundException(Long id) {
      super("Category with id: " + id + " not found");
    }
}
