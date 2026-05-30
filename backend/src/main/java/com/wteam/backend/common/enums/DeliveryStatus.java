package com.wteam.backend.common.enums;

import lombok.Getter;

/**
 * The enum Delivery status.
 */
@Getter
public enum DeliveryStatus {
    /**
     * Pending delivery status.
     */
    PENDING,
    /**
     * Sent delivery status.
     */
    SENT,
    /**
     * Delivered delivery status.
     */
    DELIVERED,
    /**
     * Returned delivery status.
     */
    RETURNED
}
