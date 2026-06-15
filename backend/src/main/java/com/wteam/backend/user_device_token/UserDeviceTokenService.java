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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDeviceTokenService {
    private final UserDeviceTokenRepository repository;
    private final UserRepository userRepository;
    private final UserDeviceTokenMapper mapper;

    @Transactional(readOnly = true)
    public List<UserDeviceTokenResponse> getTokensByUserId(Long userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public UserDeviceTokenResponse saveToken(Long userId, UserDeviceTokenRequest request) {
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
    public void deleteToken(String token) {
        repository.deleteByToken(token);
        log.info("Token deleted: {}", token);
    }

    @Transactional
    public void deleteTokenForUser(String token, Long userId) {
        Optional<UserDeviceToken> tokenOpt = repository.findByToken(token);

        UserDeviceToken deviceToken = tokenOpt.orElseThrow(() -> new UserDeviceTokenNotFoundException(token));

        if (!deviceToken.getUser().getId().equals(userId)) {
            throw new UserDeviceTokenAccessDeniedException();
        }

        repository.delete(deviceToken);
        log.info("Token deleted by user {}: {}", userId, token);
    }
}
