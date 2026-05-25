package com.wteam.backend.user;

import com.wteam.backend.common.enums.Role;
import com.wteam.backend.common.enums.VerificationStatus;
import com.wteam.backend.exception.user_profile.ProfileIncompleteException;
import com.wteam.backend.exception.user_profile.ProfileNotFoundException;
import com.wteam.backend.exception.user.UserNotFoundException;
import com.wteam.backend.user.dto.UserResponse;
import com.wteam.backend.user_profile.UserProfile;
import com.wteam.backend.user_profile.dto.UserProfileRequest;
import com.wteam.backend.user_profile.dto.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    //
    // USER
    //
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAllWithProfile(pageable)
                .map(userMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        Assert.hasText(email, "Email must not be empty");

        return userRepository.findByEmail(email)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsersWhoIsActive(Pageable pageable) {
        return userRepository.findAllByIsActiveTrue(pageable)
                .map(userMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsersWhoIsNotActive(Pageable pageable) {
        return userRepository.findAllByIsActiveFalse(pageable)
                .map(userMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsersByRole(Role role, Pageable pageable) {
        return userRepository.findAllByRole(role, pageable)
                .map(userMapper::toResponse);
    }

    @Transactional
    public void updateRole(Role role, Long id) {
        Assert.notNull(role, "Role must not be null");

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setRole(role);
    }

    @Transactional
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                        .orElseThrow(() -> new UserNotFoundException(id));

        userRepository.delete(user);
    }

    @Transactional
    public void deactivateUser(Long id, Long adminId, String reason) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setActive(false);
        user.setBlockedAt(Instant.now());
        user.setBlockedById(adminId);
        user.setBlockReason(reason);
    }

    @Transactional
    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setActive(true);
        user.setBlockedAt(null);
        user.setBlockedById(null);
        user.setBlockReason(null);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    // USER PROFILE
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        UserProfile profile = Optional.ofNullable(user.getUserProfile())
                .orElseThrow(() -> new ProfileNotFoundException(userId));

        return userMapper.toProfileResponse(profile);
    }

    @Transactional
    public UserResponse updateProfile(Long userId, UserProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        UserProfile userProfile = Optional.ofNullable(user.getUserProfile())
                        .orElseThrow(() -> new ProfileNotFoundException(userId));

        userMapper.updateProfileFromRequest(request, userProfile);
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateVerificationStatus(Long userId, VerificationStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        UserProfile userProfile = Optional.ofNullable(user.getUserProfile())
                        .orElseThrow(() -> new ProfileNotFoundException(userId));

        userProfile.setVerificationStatus(status);
        return userMapper.toResponse(user);
    }



    @Transactional(readOnly = true)
    public void validateUserCanPlaceOffers(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        UserProfile profile = Optional.ofNullable(user.getUserProfile())
                .orElseThrow(() -> new ProfileNotFoundException(userId));

        if (profile.getPhoneNumber() == null || profile.getBirthDate() == null ||
            profile.getVerificationStatus() != VerificationStatus.VERIFIED
        ) {
            throw new ProfileIncompleteException(userId);
        }
    }

}
