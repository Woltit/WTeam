package com.wteam.backend.common.enums;

import lombok.Getter;

/**
 * The enum Notification type.
 */
@Getter
public enum NotificationType {
    /**
     * Booking request notification type.
     */
    BOOKING_REQUEST,
    /**
     * Booking approved notification type.
     */
    BOOKING_APPROVED,
    /**
     * Booking rejected notification type.
     */
    BOOKING_REJECTED,
    /**
     * Booking cancelled notification type.
     */
    BOOKING_CANCELLED,
    /**
     * Payment received notification type.
     */
    PAYMENT_RECEIVED,
    /**
     * Review left notification type.
     */
    REVIEW_LEFT,
    /**
     * Verification approved notification type.
     */
    VERIFICATION_APPROVED,
    /**
     * Verification rejected notification type.
     */
    VERIFICATION_REJECTED,
    /**
     * Dispute opened notification type.
     */
    DISPUTE_OPENED
}
