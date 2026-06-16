package com.wteam.backend.user_profile;

import com.wteam.backend.common.enums.VerificationStatus;
import com.wteam.backend.exception.user_profile.ProfileIncompleteException;
import com.wteam.backend.exception.user_profile.ProfileNotFoundException;
import com.wteam.backend.user_profile.dto.PendingProfileResponse;
import com.wteam.backend.user_profile.dto.PublicProfileResponse;
import com.wteam.backend.user_profile.dto.UserProfileRequest;
import com.wteam.backend.user_profile.dto.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.wteam.backend.cloudinary.ImageService;
import com.wteam.backend.exception.cloudinary.ImageUploadException;
import com.wteam.backend.exception.user.ProfileAlreadyVerifiedException;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;
    private final ImageService imageService;

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        return userProfileMapper.toProfileResponse(getUserProfile(userId));
    }

    @Transactional(readOnly = true)
    public PublicProfileResponse getPublicProfile(Long userId) {
        return userProfileMapper.toPublicProfileResponse(getUserProfile(userId));
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UserProfileRequest request) {
        UserProfile userProfile = getUserProfile(userId);
        userProfileMapper.updateProfileFromRequest(request, userProfile);
        return userProfileMapper.toProfileResponse(userProfileRepository.save(userProfile));
    }

    @Transactional
    public UserProfileResponse updateVerificationStatus(Long userId, VerificationStatus status) {
        UserProfile userProfile = getUserProfile(userId);
        userProfile.setVerificationStatus(status);
        return userProfileMapper.toProfileResponse(userProfileRepository.save(userProfile));
    }

    @Transactional
    public void uploadAvatar(Long userId, MultipartFile file) {
        try {
            UserProfile userProfile = getUserProfile(userId);
            String avatarUrl = imageService.uploadImage(file);
            userProfile.setAvatarUrl(avatarUrl);
            userProfileRepository.save(userProfile);
        } catch (java.io.IOException e) {
            throw new ImageUploadException("Failed to upload avatar", e);
        }
    }

    @Transactional(readOnly = true)
    public Page<PendingProfileResponse> getPendingProfiles(Pageable pageable) {
        return userProfileRepository.findAllByVerificationStatus(VerificationStatus.PENDING, pageable)
                .map(userProfileMapper::toPendingProfileResponse);
    }

    @Transactional
    public UserProfileResponse submitForVerification(Long userId) {
        UserProfile userProfile = getUserProfile(userId);

        if (userProfile.getPhoneNumber() == null || userProfile.getBirthDate() == null
                || userProfile.getFirstName().isBlank() || userProfile.getLastName().isBlank()) {
            throw new ProfileIncompleteException(userId);
        }

        if (userProfile.getVerificationStatus() == VerificationStatus.VERIFIED) {
            throw new ProfileAlreadyVerifiedException("Profile is already verified");
        }

        userProfile.setVerificationStatus(VerificationStatus.PENDING);
        return userProfileMapper.toProfileResponse(userProfileRepository.save(userProfile));
    }

    @Transactional(readOnly = true)
    public void validateUserCanPlaceOffers(Long userId) {
        UserProfile profile = getUserProfile(userId);

        if (profile.getPhoneNumber() == null ||
                profile.getBirthDate() == null ||
                profile.getVerificationStatus() != VerificationStatus.VERIFIED) {
            throw new ProfileIncompleteException(userId);
        }
    }

    private UserProfile getUserProfile(Long userId) {
        return userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));
    }
}
