package com.wteam.backend.common.enums;

import lombok.Getter;

/**
 * The enum Renting status.
 */
@Getter
public enum RentingStatus {
    /**
     * Active renting status.
     */
    ACTIVE,
    /**
     * Rented renting status.
     */
    RENTED,
    /**
     * Inactive renting status.
     */
    INACTIVE,
    /**
     * Deleted renting status.
     */
    DELETED
}
