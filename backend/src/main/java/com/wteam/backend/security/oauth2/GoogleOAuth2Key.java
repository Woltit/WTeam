package com.wteam.backend.security.oauth2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GoogleOAuth2Key {
    EMAIL("email"),
    LAST_NAME("family_name"),
    FIRST_NAME("given_name"),
    PICTURE("picture");

    private final String key;
}
