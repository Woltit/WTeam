package com.wteam.backend.common.enums;

import lombok.Getter;

/**
 * The enum Transaction status.
 */
@Getter
public enum TransactionStatus {
    /**
     * Pending transaction status.
     */
    PENDING,
    /**
     * Success transaction status.
     */
    SUCCESS,
    /**
     * Failed transaction status.
     */
    FAILED,
    /**
     * Refunded transaction status.
     */
    REFUNDED
}
