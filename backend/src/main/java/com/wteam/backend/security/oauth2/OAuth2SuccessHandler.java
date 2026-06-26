package com.wteam.backend.security.oauth2;

import com.wteam.backend.cookies.CookieService;
import com.wteam.backend.refresh_token.RefreshTokenService;
import com.wteam.backend.security.SecurityUser;
import com.wteam.backend.security.jwt.JwtService;
import com.wteam.backend.user.User;
import com.wteam.backend.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * The type O auth 2 success handler.
 */
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final CookieService cookieService;

    @Value("${app.frontend.oauth2-redirect-uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(@NonNull HttpServletRequest request,
                                        @NonNull HttpServletResponse response,
                                        Authentication authentication
    ) throws IOException {
        SecurityUser securityUser;
        User dbUser;
        Object principal = authentication.getPrincipal();

        if (principal == null) {
            throw new IllegalArgumentException("Principal is null");
        }

        switch (principal) {
            case SecurityUser su -> {
                securityUser = su;
                dbUser = securityUser.getOriginalUser();
            }
            case OAuth2User oAuth2User -> {
                // Google uses OIDC — principal is DefaultOidcUser (implements OidcUser → OAuth2User)
                String email = oAuth2User instanceof OidcUser oidcUser
                        ? oidcUser.getEmail()
                        : (String) oAuth2User.getAttribute("email");
                dbUser = userRepository.findByEmail(email)
                        .orElseThrow(() -> new IllegalStateException("OAuth2 user not found in DB: " + email));
                securityUser = new SecurityUser(dbUser);
            }
            default -> throw new IllegalStateException("Unexpected principal type: " + principal.getClass().getName());
        }

        String accessToken = jwtService.generateToken(securityUser);
        var refreshToken = refreshTokenService.generateRefreshToken(dbUser);

        cookieService.setRefreshTokenCookie(response, refreshToken.getTokenHash());

        String targetUri = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("accessToken", accessToken)
                .build()
                .toUriString();

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUri);
    }
}
