package com.wteam.backend.security.oauth2;

import com.wteam.backend.security.SecurityUser;
import com.wteam.backend.security.jwt.JwtService;
import com.wteam.backend.user.UserRepository;
import jakarta.servlet.ServletException;
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
    private final UserRepository userRepository;

    @Value("${app.frontend.oauth2-redirect-uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(@NonNull HttpServletRequest request,
                                        @NonNull HttpServletResponse response,
                                        Authentication authentication
    ) throws IOException, ServletException {
        SecurityUser securityUser;
        Object principal = authentication.getPrincipal();

        if (principal instanceof SecurityUser su) {
            securityUser = su;
        } else if (principal instanceof OAuth2User oAuth2User) {
            // Google uses OIDC — principal is DefaultOidcUser (implements OidcUser → OAuth2User)
            String email = oAuth2User instanceof OidcUser oidcUser
                    ? oidcUser.getEmail()
                    : (String) oAuth2User.getAttribute("email");
            var user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("OAuth2 user not found in DB: " + email));
            securityUser = new SecurityUser(user);
        } else {
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass().getName());
        }

        String token = jwtService.generateToken(securityUser);

        String targetUri = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", token)
                .build()
                .toUriString();

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUri);
    }
}
