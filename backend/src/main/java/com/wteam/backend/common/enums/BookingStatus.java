package com.wteam.backend.common.enums;

import lombok.Getter;

/**
 * The enum Booking status.
 */
@Getter
public enum BookingStatus {
    /**
     * Pending booking status.
     */
    PENDING,
    /**
     * Approved booking status.
     */
    APPROVED,
    /**
     * Rejected booking status.
     */
    REJECTED,
    /**
     * Paid booking status.
     */
    PAID,
    /**
     * In progress booking status.
     */
    IN_PROGRESS,
    /**
     * Completed booking status.
     */
    COMPLETED,
    /**
     * Cancelled booking status.
     */
    CANCELLED,
    /**
     * Dispute booking status.
     */
    DISPUTE
}
