package com.wteam.backend.common.enums;

import lombok.Getter;

/**
 * The enum Item condition.
 */
@Getter
public enum ItemCondition {
    /**
     * Ideal item condition.
     */
    IDEAL,
    /**
     * Good item condition.
     */
    GOOD,
    /**
     * Norm item condition.
     */
    NORM,
    /**
     * Bad item condition.
     */
    BAD,
    /**
     * Needs repairing item condition.
     */
    NEEDS_REPAIRING
}
