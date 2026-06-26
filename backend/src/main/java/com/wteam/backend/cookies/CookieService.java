package com.wteam.backend.cookies;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class CookieService {
    @Value("${auth.cookie.refresh-path:/api/v1/auth/refresh}")
    private String refreshCookiePath;

    @Value("${auth.cookie.max-age:604800}")
    private int cookieMaxAge;

    public void setRefreshTokenCookie(final HttpServletResponse response, final String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath(refreshCookiePath);
        cookie.setMaxAge(cookieMaxAge);
        response.addCookie(cookie);
    }

    public void clearRefreshTokenCookie(final HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setHttpOnly(true);
        cookie.setPath(refreshCookiePath);
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }
}
