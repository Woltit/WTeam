package com.wteam.backend.booking.dto;

import com.wteam.backend.common.enums.BookingStatus;
import jakarta.validation.constraints.NotNull;

public record BookingStatusUpdateRequest(
        @NotNull(message = "Status is required")
        BookingStatus status,

        String cancellationReason
) {}
