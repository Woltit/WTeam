package com.wteam.backend.item.dto;

import com.wteam.backend.common.enums.ItemCondition;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

import static com.wteam.backend.common.validation.ValidationConstants.Item.TITLE_MAX_LENGTH;

public record ItemRequest(
        @NotNull(message = "Category ID cannot be null")
        Long categoryId,

        @NotBlank(message = "Title cannot be blank")
        @Size(max = TITLE_MAX_LENGTH, message = "Title cannot exceed " + TITLE_MAX_LENGTH + " characters")
        String title,

        @NotBlank(message = "Description cannot be blank")
        String description,

        List<String> tags,

        @NotNull(message = "Condition cannot be null")
        ItemCondition condition,

        @NotNull(message = "Price per day is required")
        @Positive(message = "Price must be greater than zero")
        BigDecimal pricePerDay,

        @Positive(message = "Price per week must be greater than zero")
        BigDecimal pricePerWeek,

        @NotNull(message = "Deposit amount is required")
        @PositiveOrZero(message = "Deposit cannot be negative")
        BigDecimal depositAmount,

        @NotBlank(message = "City cannot be blank")
        String city,

        @NotBlank(message = "Address cannot be blank")
        String address,

        BigDecimal latitude,

        BigDecimal longitude
) {}
