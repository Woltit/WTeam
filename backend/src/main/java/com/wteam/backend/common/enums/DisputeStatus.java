package com.wteam.backend.common.enums;

import lombok.Getter;

/**
 * The enum Dispute status.
 */
@Getter
public enum DisputeStatus {
    /**
     * Open dispute status.
     */
    OPEN,
    /**
     * Under review dispute status.
     */
    UNDER_REVIEW,
    /**
     * Resolved dispute status.
     */
    RESOLVED,
    /**
     * Closed dispute status.
     */
    CLOSED
}
