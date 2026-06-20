package com.wteam.backend.security.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.Base64;
import java.util.Optional;

public final class CookieUtils {
    private CookieUtils() {
        throw new UnsupportedOperationException("CookieUtils class cannot be instantiated - utility class");
    }

    public static Optional<Cookie> getCookie(final HttpServletRequest request, final String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }

        return Optional.empty();
    }

    public static void addCookie(final HttpServletResponse response, final String name, final String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    public static void deleteCookie(final HttpServletRequest request, final HttpServletResponse response, final String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    break;
                }
            }
        }
    }

    public static String serialize(final Object object) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {

            oos.writeObject(object);
            return Base64.getUrlEncoder().encodeToString(bos.toByteArray());

        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to serialize object", e);
        }
    }

    public static <T> T deserialize(final Cookie cookie, final Class<T> clazz) {
        byte[] decodedBytes = Base64.getUrlDecoder().decode(cookie.getValue());

        try (ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes);
             ObjectInputStream ois = new ObjectInputStream(bis)) {

            Object obj = ois.readObject();
            return clazz.cast(obj);

        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalArgumentException("Failed to deserialize object from cookie", e);
        }
    }
}

