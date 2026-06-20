package com.wteam.backend.user_device_token;

import com.wteam.backend.exception.user.UserNotFoundException;
import com.wteam.backend.exception.user_device_token.UserDeviceTokenAccessDeniedException;
import com.wteam.backend.exception.user_device_token.UserDeviceTokenNotFoundException;
import com.wteam.backend.user.User;
import com.wteam.backend.user.UserRepository;
import com.wteam.backend.user_device_token.dto.UserDeviceTokenRequest;
import com.wteam.backend.user_device_token.dto.UserDeviceTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDeviceTokenService {
    private final UserDeviceTokenRepository repository;
    private final UserRepository userRepository;
    private final UserDeviceTokenMapper mapper;
    private final CacheManager cacheManager;

    @Transactional(readOnly = true)
    @Cacheable(value = "userDeviceTokens", key = "#userId")
    public List<UserDeviceTokenResponse> getTokensByUserId(final Long userId) {
        return new java.util.ArrayList<>(repository.findByUserId(userId)
                .stream()
                .map(mapper::toResponse)
                .toList());
    }

    @Transactional
    @CacheEvict(value = "userDeviceTokens", key = "#userId")
    public UserDeviceTokenResponse saveToken(final Long userId, final UserDeviceTokenRequest request) {
        Optional<UserDeviceToken> existingTokenOpt = repository.findByToken(request.token());

        if (existingTokenOpt.isPresent()) {
            UserDeviceToken existingToken = existingTokenOpt.get();

            if (!existingToken.getUser().getId().equals(userId)) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException(userId));

                existingToken.setUser(user);
                existingToken.setType(request.type());
                return mapper.toResponse(repository.save(existingToken));
            }

            return mapper.toResponse(existingToken);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        UserDeviceToken newToken = UserDeviceToken.builder()
                .user(user)
                .token(request.token())
                .type(request.type())
                .build();

        return mapper.toResponse(repository.save(newToken));
    }

    @Transactional
    public void deleteToken(final String token) {
        repository.findByToken(token).ifPresent(deviceToken -> {
            Long userId = deviceToken.getUser().getId();
            repository.delete(deviceToken);

            Objects.requireNonNull(cacheManager.getCache("userDeviceTokens")).evict(userId);

            log.info("Token deleted: {}", token);
        });
    }

    @Transactional
    @CacheEvict(value = "userDeviceTokens", key = "#userId")
    public void deleteTokenForUser(final String token, final Long userId) {
        Optional<UserDeviceToken> tokenOpt = repository.findByToken(token);

        UserDeviceToken deviceToken = tokenOpt.orElseThrow(() -> new UserDeviceTokenNotFoundException(token));

        if (!deviceToken.getUser().getId().equals(userId)) {
            throw new UserDeviceTokenAccessDeniedException();
        }

        repository.delete(deviceToken);
        log.info("Token deleted by user {}: {}", userId, token);
    }
}
