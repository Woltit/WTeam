package com.wteam.backend.booking.dto;

import com.wteam.backend.common.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookingResponse(
        Long id,
        Long itemId,
        Long renterId,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalPrice,
        BigDecimal depositTotal,
        BigDecimal pricePerDaySnapshot,
        BookingStatus status,
        String cancellationReason
) {}
