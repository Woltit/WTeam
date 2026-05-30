package com.wteam.backend.security.oauth2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The enum Google o auth 2 key.
 */
@Getter
@RequiredArgsConstructor
public enum GoogleOAuth2Key {
    /**
     * Email google o auth 2 key.
     */
    EMAIL("email"),
    /**
     * Last name google o auth 2 key.
     */
    LAST_NAME("family_name"),
    /**
     * First name google o auth 2 key.
     */
    FIRST_NAME("given_name"),
    /**
     * Picture google o auth 2 key.
     */
    PICTURE("picture");

    private final String key;
}
