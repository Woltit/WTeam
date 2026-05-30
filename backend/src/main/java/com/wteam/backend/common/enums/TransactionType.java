package com.wteam.backend.common.enums;

import lombok.Getter;

/**
 * The enum Transaction type.
 */
@Getter
public enum TransactionType {
    /**
     * Rent payment transaction type.
     */
    RENT_PAYMENT,
    /**
     * Deposit hold transaction type.
     */
    DEPOSIT_HOLD,
    /**
     * Deposit refund transaction type.
     */
    DEPOSIT_REFUND,
    /**
     * Compensation transaction type.
     */
    COMPENSATION
}
