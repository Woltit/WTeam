package com.wteam.backend.user;

import com.wteam.backend.common.enums.Role;
import com.wteam.backend.exception.user.UserNotFoundException;
import com.wteam.backend.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Boolean isActive, Role role, Pageable pageable) {
        Page<User> users;

        if (isActive == null && role == null) {
            users = userRepository.findAll(pageable);
        } else if (isActive != null && role == null) {
            users = isActive
                    ? userRepository.findAllByIsActiveTrue(pageable)
                    : userRepository.findAllByIsActiveFalse(pageable);
        } else if (isActive == null) {
            users = userRepository.findAllByRole(role, pageable);
        } else {
            users = userRepository.findAllByIsActiveAndRole(isActive, role, pageable);
        }

        return users.map(userMapper::toResponse);
    }

    /**
     * Gets user by userId.
     *
     * @param userId the userId
     * @return the user by userId
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    /**
     * Gets user by email.
     *
     * @param email the email
     * @return the user by email
     */
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        Assert.hasText(email, "Email must not be empty");

        return userRepository.findByEmail(email)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    /**
     * Update role.
     *
     * @param role the role
     * @param userId   the userId
     */
    @Transactional
    public void updateRole(Role role, Long userId) {
        Assert.notNull(role, "Role must not be null");

        User user = getUser(userId);
        user.setRole(role);
    }

    /**
     * Delete user by userId.
     *
     * @param userId the userId
     */
    @Transactional
    public void deleteUserById(Long userId) {
        userRepository.delete(getUser(userId));
    }

    /**
     * Deactivate user.
     *
     * @param userId      the userId
     * @param adminId the admin userId
     * @param reason  the reason
     */
    @Transactional
    public void deactivateUser(Long userId, Long adminId, String reason) {
        User user = getUser(userId);

        user.setActive(false);
        user.setBlockedAt(Instant.now());
        user.setBlockedById(adminId);
        user.setBlockReason(reason);
    }

    /**
     * Activate user.
     *
     * @param userId the userId
     */
    @Transactional
    public void activateUser(Long userId) {
        User user = getUser(userId);

        user.setActive(true);
        user.setBlockedAt(null);
        user.setBlockedById(null);
        user.setBlockReason(null);
    }

    /**
     * Exists by userId boolean.
     *
     * @param userId the userId
     * @return the boolean
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }


    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
