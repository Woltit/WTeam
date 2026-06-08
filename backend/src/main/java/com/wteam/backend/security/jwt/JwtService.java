package com.wteam.backend.security.jwt;

import com.wteam.backend.security.SecurityUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Сервіс для роботи з JSON Web Tokens (JWT).
 * <p>
 * Клас відповідає за генерацію, парсинг та валідацію криптографічно підписаних токенів доступу (Access Tokens).
 * </p>
 *
 * @see SecurityUser
 * @see Jwts
 */
@Component
public class JwtService {

    /**
     * Секретний ключ у кодуванні Base64, що використовується для підпису та верифікації JWT токенів.
     * Завантажується зі змінних оточення додатка.
     */
    @Value("${jwt.token.signing_key}")
    private String jwtSigningKey;

    /**
     * Час життя токена в мілісекундах (expiration time).
     * Завантажується зі змінних оточення додатка.
     */
    @Value("${jwt.token.expiration}")
    private long expiration;

    /**
     * Витягує унікальне ім'я користувача (subject) з тіла JWT токена. У цій системі ним є email.
     *
     * @param token JWT токен у вигляді рядка.
     * @return електронна пошта (email) користувача, що міститься в токені.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Генерує новий JWT токен для вказаного користувача.
     * <p>
     * Якщо переданий об'єкт є екземпляром {@link SecurityUser}, у токен автоматично додаються
     * додаткові безпечні клейми (claims): {@code id} та {@code role} користувача.
     * </p>
     *
     * @param userDetails об'єкт користувача у контексті Spring Security.
     * @return згенерований та компактно упакований JWT токен.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        if (userDetails instanceof SecurityUser securityUser) {
            claims.put("id", securityUser.getId());
            claims.put("role", securityUser.getRole());
        }

        return generateToken(claims, userDetails);
    }

    /**
     * Перевіряє валідність JWT токена.
     * <p>
     * Токен вважається валідним, якщо ім'я користувача (email) в токені збігається з ім'ям
     * у переданому {@link UserDetails}, і термін дії токена ще не закінчився.
     * </p>
     *
     * @param token       JWT токен для перевірки.
     * @param userDetails об'єкт користувача, з яким порівнюються дані токена.
     * @return {@code true}, якщо токен повністю валідний; {@code false} в іншому випадку.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = userDetails.getUsername();
            return (username.equals(extractUsername(token))) && !isTokenExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Універсальний утилітарний метод для витягування конкретного клейма (claim) з токена
     * за допомогою функціонального інтерфейсу-резолвера.
     *
     * @param <T>            тип значення, яке потрібно отримати.
     * @param token          JWT токен.
     * @param claimsResolver функція, що визначає, яке саме поле потрібно витягти з {@link Claims}.
     * @return витягнуте значення клейма.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Внутрішній метод для конструювання та підпису JWT токена.
     * Sets subject, дату створення, дату закінчення дії та підписує ключ алгоритмом шифрування.
     */
    private String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Перевіряє, чи закінчився термін дії (термін придатності) JWT токена.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Витягує дату закінчення дії токена (Expiration Date).
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Парсить токен, валідує його криптографічний підпис за допомогою ключа і повертає повний набір клеймів (Payload).
     *
     * @throws io.jsonwebtoken.JwtException якщо токен підроблений, пошкоджений або його термін дії закінчився.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Декодує конфігураційний рядок Base64 і створює об'єкт {@link SecretKey},
     * необхідний для алгоритмів сімейства HMAC-SHA (наприклад, HS512).
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
