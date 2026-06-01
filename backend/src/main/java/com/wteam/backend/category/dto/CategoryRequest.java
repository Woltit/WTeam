package com.wteam.backend.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static com.wteam.backend.common.validation.ValidationConstants.Category.*;

public record CategoryRequest(
        Long parentId,

        @NotBlank(message = "Category name cannot be blank")
        @Size(max = NAME_MAX_LENGTH, message = "Category name cannot exceed " + NAME_MAX_LENGTH + " characters")
        String name,

        @NotBlank(message = "Category slug cannot be blank")
        @Size(max = SLUG_MAX_LENGTH, message = "Category slug cannot exceed " + SLUG_MAX_LENGTH + " characters")
        String slug,

        String iconUrl
) {}
