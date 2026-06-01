package com.wteam.backend.refresh_token;

import com.wteam.backend.common.entity.BaseEntityPart;
import com.wteam.backend.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

import static com.wteam.backend.common.validation.ValidationConstants.RefreshToken.TOKEN_HASH_MAX_LENGTH;

/**
 * Сутність, що представляє токен оновлення (Refresh Token) для сесії користувача.
 * Використовується для отримання нових Access Token-ів без повторної автентифікації.
 *
 * @see com.wteam.backend.user.User
 */
@Entity
@Table(name = "refresh_tokens")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class RefreshToken extends BaseEntityPart {
    /**
     * Унікальний ідентифікатор токена.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "refresh_tokens_gen")
    @SequenceGenerator(name = "refresh_tokens_gen", sequenceName = "refresh_tokens_id_seq", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    
    /**
     * Користувач, якому належить токен.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    /**
     * Хеш токена оновлення.
     */
    @Column(name = "token_hash", length = TOKEN_HASH_MAX_LENGTH, nullable = false, unique = true)
    private String tokenHash;

    /**
     * Час закінчення дії токена.
     */
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    /**
     * Час відкликання токена (якщо він був анульований раніше терміну).
     */
    @Column(name = "revoked_at")
    private Instant revokedAt;
}
