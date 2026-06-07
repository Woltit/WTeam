package com.wteam.backend.security;

import com.wteam.backend.security.jwt.JwtAuthFilter;
import com.wteam.backend.security.oauth2.CustomOAuth2UserService;
import com.wteam.backend.security.oauth2.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Глобальний клас констант конфігурації безпеки додатка.
 * <p>
 * Налаштовує ланцюжок фільтрів безпеки (CORS, CSRF, сесії), правила доступу до HTTP-ендпоінтів,
 * підключає кастомний JWT-фільтр та перевизначає механізми хешування паролів.
 * </p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    /**
     * Конфігурує основний ланцюжок фільтрів безпеки (Security Filter Chain).
     * <p>
     * Налаштування включають:
     * 1. Вимкнення захисту CSRF (оскільки додаток використовує токени, а не сесії).
     * 2. Підключення кастомних правил CORS.
     * 3. Переведення сесій у режим {@link SessionCreationPolicy#STATELESS} (без збереження стану).
     * 4. Реєстрацію провайдера автентифікації.
     * 5. Визначення правил доступу: публічний доступ до авторизації, всі інші запити — захищені.
     * 6. Додавання {@link JwtAuthFilter} перед стандартним фільтром автентифікації користувачів.
     * </p>
     *
     * @param http об'єкт для конструювання ланцюжка фільтрів.
     * @return змонтований об'єкт {@link SecurityFilterChain}.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider(userDetailsService))
                .authorizeHttpRequests(auth -> auth
                        // дозволяється публічний доступ до аутентифікації
                        .requestMatchers("/auth/**").permitAll()
                        // можна отримувати доступ до товарів без реєстрації
                        .requestMatchers(HttpMethod.GET, "/items/**").permitAll()
                        // всі інші запити вимагають обов'язкової аутентифікації
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                )
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }

    /**
     * Налаштовує правила CORS (Cross-Origin Resource Sharing) для взаємодії з фронтендом.
     * <p>
     * Дозволяє запити з локального порту Vite/React (5173), обмежує перелік дозволених
     * HTTP-методів та заголовків, а також дозволяє передачу cookie та заголовків авторизації.
     * </p>
     *
     * @return конфігураційне джерело CORS {@link CorsConfigurationSource}.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
                List.of("http://localhost:5173", "https://localhost:5173", "http://localhost:5174")
        );
        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );
        configuration.setAllowedHeaders(
                List.of("Authorization", "Content-Type")
        );
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Визначає кодувальник паролів за замовчуванням.
     * <p>
     * Використовує стійкий алгоритм криптографічного хешування BCrypt.
     * </p>
     *
     * @return об'єкт {@link PasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Конфігурує стандартний провайдер автентифікації на основі даних із бази даних.
     * <p>
     * Поєднує кастомний сервіс пошуку користувачів {@link UserDetailsService}
     * та інструмент для перевірки хешованих паролів {@link BCryptPasswordEncoder}.
     * </p>
     *
     * @param userDetailsService сервіс для пошуку користувачів у БД.
     * @return налаштований об'єкт {@link AuthenticationProvider}.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(
                userDetailsService
        );
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
        return config.getAuthenticationManager();
    }
}
