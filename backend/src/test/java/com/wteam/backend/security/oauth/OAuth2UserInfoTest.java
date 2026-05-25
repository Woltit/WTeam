package com.wteam.backend.security.oauth;

import com.wteam.backend.common.enums.AuthProvider;
import com.wteam.backend.security.oauth2.OAuth2UserInfo;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OAuth2UserInfoTest {

    @Test
    void shouldSuccessfullyMapGoogleAttributes() {
        final String firstName = "John";
        final String lastName = "Doe";

        Map<String, Object> attributes = Map.of(
                "email", "test@gmail.com",
                "given_name", firstName,
                "family_name", lastName,
                "picture", "https://avatar.url"
        );

        OAuth2UserInfo userInfo = OAuth2UserInfo.fromProvider("google", attributes);

        assertEquals("test@gmail.com", userInfo.email());
        assertEquals(firstName, userInfo.firstName());
        assertEquals(lastName, userInfo.lastName());
        assertEquals("https://avatar.url", userInfo.avatarUrl());
        assertEquals(AuthProvider.GOOGLE, userInfo.provider());
    }

    @Test
    void shouldSetDefaultValuesIfNameIsMissingForGoogle() {
        Map<String, Object> attributes = Map.of("email", "no-name@gmail.com");

        OAuth2UserInfo userInfo = OAuth2UserInfo.fromProvider("google", attributes);

        assertEquals("User", userInfo.firstName());
        assertEquals("", userInfo.lastName());
    }

    @Test
    void shouldThrowExceptionForAppleStub() {
        Map<String, Object> attributes = Map.of("email", "test@apple.com");

        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> OAuth2UserInfo.fromProvider("apple", attributes)
        );

        assertTrue(exception.getMessage().contains("is not supported yet"));
    }

    @Test
    void shouldThrowExceptionForLocalProvider() {
        Map<String, Object> attributes = Map.of("email", "local@test.com");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> OAuth2UserInfo.fromProvider("local", attributes)
        );

        assertTrue(exception.getMessage().contains("Unknown provider"));
    }
}