package com.wteam.backend.security.oauth2;

import com.wteam.backend.common.enums.AuthProvider;

import java.util.Locale;
import java.util.Map;

/**
 * The type O auth 2 user info.
 */
public record OAuth2UserInfo(
    String email,
    String lastName,
    String firstName,
    String avatarUrl,
    AuthProvider provider
) {
    /**
     * From provider o auth 2 user info.
     *
     * @param registrationId the registration id
     * @param attributes     the attributes
     * @return the o auth 2 user info
     */
    public static OAuth2UserInfo fromProvider(final String registrationId, final Map<String, Object> attributes) {
        final AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase(Locale.ROOT));

        return switch (provider) {
            case GOOGLE -> new OAuth2UserInfo(
                    (String) attributes.get(GoogleOAuth2Key.EMAIL.getKey()),
                    (String) attributes.getOrDefault(GoogleOAuth2Key.LAST_NAME.getKey(), ""),
                    (String) attributes.getOrDefault(GoogleOAuth2Key.FIRST_NAME.getKey(), "User"),
                    (String) attributes.get(GoogleOAuth2Key.PICTURE.getKey()),
                    provider
            );

            case APPLE -> throw new UnsupportedOperationException(
                    "Authorization via " + provider.name() + " is not supported yet"
            );

            default -> throw new IllegalArgumentException("Unknown provider " + provider.name());
        };
    }
}
