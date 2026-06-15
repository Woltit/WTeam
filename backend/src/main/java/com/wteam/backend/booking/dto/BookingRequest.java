package com.wteam.backend.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BookingRequest(
        @NotNull(message = "Item ID is required")
        Long itemId,

        // renterId часто береться з контексту безпеки (SecurityContext),
        // але якщо авторизації ще немає, можна передавати в DTO:
        Long renterId,

        @NotNull(message = "Start date is required")
        @FutureOrPresent(message = "Start date cannot be in the past")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        @FutureOrPresent(message = "End date cannot be in the past")
        LocalDate endDate
) {}
