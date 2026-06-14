package com.wteam.backend.refresh_token;

import com.wteam.backend.exception.refresh_token.RefreshTokenInvalidException;
import com.wteam.backend.exception.refresh_token.RefreshTokenNotFoundException;
import com.wteam.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final ObjectProvider<RefreshTokenService> selfProvider;

    @Transactional
    public RefreshToken generateRefreshToken(User user) {
        String tokenHash = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken processRefreshToken(String tokenHash) {
        RefreshToken oldToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new RefreshTokenNotFoundException("Refresh accessToken not found"));

        if (oldToken.getRevokedAt() != null) {
            refreshTokenRepository.delete(oldToken);
            throw new RefreshTokenInvalidException("Refresh accessToken has been revoked");
        }

        if (oldToken.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(oldToken);
            throw new RefreshTokenInvalidException("Refresh accessToken has expired");
        }

        User user = oldToken.getUser();
        refreshTokenRepository.delete(oldToken);

        RefreshTokenService self = selfProvider.getIfAvailable();
        if (self == null) {
            throw new IllegalStateException("Internal server error: self provider was not found");
        }

        return self.generateRefreshToken(user);
    }
}
