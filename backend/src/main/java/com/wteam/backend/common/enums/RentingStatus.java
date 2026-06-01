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
    AVAILABLE,
    /**
     * Rented renting status.
     */
    RENTED,
    /**
     * Hidden renting status.
     */
    HIDDEN,
    /**
     * Archived renting status.
     */
    ARCHIVED
}
