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

import static com.wteam.backend.common.constants.ValidationConstants.RefreshToken.*;

@Entity
@Table(name = "refresh_token")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class RefreshToken extends BaseEntityPart {
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "token_hash", length = TOKEN_HASH_MAX_LENGTH, nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;
}
