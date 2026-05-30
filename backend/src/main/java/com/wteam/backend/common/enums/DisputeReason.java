package com.wteam.backend.common.enums;

import lombok.Getter;

/**
 * The enum Dispute reason.
 */
@Getter
public enum DisputeReason {
    /**
     * Item damaged dispute reason.
     */
    ITEM_DAMAGED,
    /**
     * Item not returned dispute reason.
     */
    ITEM_NOT_RETURNED,
    /**
     * Payment issue dispute reason.
     */
    PAYMENT_ISSUE,
    /**
     * Other dispute reason.
     */
    OTHER
}
