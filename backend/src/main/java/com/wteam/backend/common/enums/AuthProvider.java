package com.wteam.backend.common.enums;

import lombok.Getter;

/**
 * The enum Auth provider.
 */
@Getter
public enum AuthProvider {
    /**
     * Local auth provider.
     */
    LOCAL,
    /**
     * Google auth provider.
     */
    GOOGLE,
    /**
     * Apple auth provider.
     */
    APPLE
}
