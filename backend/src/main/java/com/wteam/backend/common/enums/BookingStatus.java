package com.wteam.backend.common.enums;

import lombok.Getter;

@Getter
public enum BookingStatus {
    PENDING,
    APPROVED,
    REJECTED,
    PAID,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    DISPUTE
}
