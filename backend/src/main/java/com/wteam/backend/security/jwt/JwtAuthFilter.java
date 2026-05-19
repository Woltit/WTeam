package com.wteam.backend.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Фільтр автентифікації на основі JWT-токенів.
 * <p>
 * Цей компонент перехоплює кожен вхідний HTTP-запит (розширюючи {@link OncePerRequestFilter}, що гарантує
 * одноразове виконання за запит) для перевірки наявності JWT-токена в заголовку {@code Authorization}.
 * Якщо токен знайдено та він є валідним, дані користувача завантажуються в контекст безпеки Spring Security.
 * </p>
 *
 * @see OncePerRequestFilter
 * @see JwtService
 * @see UserDetailsService
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    /** Назва HTTP-заголовка, який має містити токен доступу. */
    private static final String AUTHORIZATION_HEADER = "Authorization";

    /** Обов'язковий префікс для токенів типу Bearer в однойменному заголовку. */
    private static final String BEARER_PREFIX = "Bearer ";

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    /**
     * Основний метод фільтрації, який аналізує заголовок автентифікації та валідує JWT-токен.
     * <p>
     * Алгоритм роботи:
     * 1. Витягує заголовок {@code Authorization}.
     * 2. Перевіряє наявність префіксу {@code Bearer }.
     * 3. Декодує токен та витягує з нього email користувача (username).
     * 4. Якщо користувач ще не автентифікований у поточному потоці запиту, завантажує його дані через {@link UserDetailsService}.
     * 5. Перевіряє термін дії та криптографічний підпис токена.
     * 6. У разі успіху створює об'єкт {@link UsernamePasswordAuthenticationToken} і зберігає його в {@link SecurityContextHolder}.
     * 7. Передає запит далі по ланцюжку фільтрів (filter chain).
     * </p>
     *
     * @param request     об'єкт HTTP-запиту.
     * @param response    об'єкт HTTP-відповіді.
     * @param filterChain ланцюжок безпеки Spring Security.
     * @throws ServletException якщо виникла помилка на рівні сервлет-контейнера.
     * @throws IOException      якщо виникла помилка введення-виведення при роботі з потоками запиту/відповіді.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            token = authHeader.substring(7);
            username = jwtService.extractUsername(token);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
