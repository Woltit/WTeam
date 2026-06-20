package com.wteam.backend.user_profile;

import com.wteam.backend.cloudinary.ImageService;
import com.wteam.backend.common.enums.VerificationStatus;
import com.wteam.backend.exception.cloudinary.ImageUploadException;
import com.wteam.backend.exception.user_profile.ProfileIncompleteException;
import com.wteam.backend.exception.user_profile.ProfileNotFoundException;
import com.wteam.backend.user_profile.dto.PendingProfileResponse;
import com.wteam.backend.user_profile.dto.PublicProfileResponse;
import com.wteam.backend.user_profile.dto.UserProfileRequest;
import com.wteam.backend.user_profile.dto.UserProfileResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserProfileService Unit Tests")
class UserProfileServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserProfileMapper userProfileMapper;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private UserProfileService userProfileService;

    @Test
    @DisplayName("getProfile should return profile response when profile exists")
    void getProfile_whenExists_shouldReturnResponse() {
        Long userId = 1L;
        UserProfile profile = new UserProfile();
        UserProfileResponse response = mock(UserProfileResponse.class);

        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(userProfileMapper.toResponse(profile)).thenReturn(response);

        UserProfileResponse result = userProfileService.getProfile(userId);

        assertNotNull(result);
        assertEquals(response, result);
    }

    @Test
    @DisplayName("getProfile should throw ProfileNotFoundException when profile does not exist")
    void getProfile_whenDoesNotExist_shouldThrowException() {
        Long userId = 1L;
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(ProfileNotFoundException.class, () -> userProfileService.getProfile(userId));
    }

    @Test
    @DisplayName("getPublicProfile should return public profile response when profile exists")
    void getPublicProfile_whenExists_shouldReturnResponse() {
        Long userId = 1L;
        UserProfile profile = new UserProfile();
        PublicProfileResponse response = mock(PublicProfileResponse.class);

        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(userProfileMapper.toPublicProfileResponse(profile)).thenReturn(response);

        PublicProfileResponse result = userProfileService.getPublicProfile(userId);

        assertNotNull(result);
        assertEquals(response, result);
    }

    @Test
    @DisplayName("updateProfile should update fields and save profile")
    void updateProfile_shouldUpdateAndSave() {
        Long userId = 1L;
        UserProfileRequest request = mock(UserProfileRequest.class);
        UserProfile profile = new UserProfile();
        UserProfileResponse response = mock(UserProfileResponse.class);

        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(userProfileRepository.save(profile)).thenReturn(profile);
        when(userProfileMapper.toResponse(profile)).thenReturn(response);

        UserProfileResponse result = userProfileService.updateProfile(userId, request);

        assertNotNull(result);
        assertEquals(response, result);
        verify(userProfileMapper).updateProfileFromRequest(request, profile);
        verify(userProfileRepository).save(profile);
    }

    @Test
    @DisplayName("updateVerificationStatus should modify status and save profile")
    void updateVerificationStatus_shouldModifyStatusAndSave() {
        Long userId = 1L;
        UserProfile profile = new UserProfile();
        profile.setVerificationStatus(VerificationStatus.UNVERIFIED);
        UserProfileResponse response = mock(UserProfileResponse.class);

        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(userProfileRepository.save(profile)).thenReturn(profile);
        when(userProfileMapper.toResponse(profile)).thenReturn(response);

        UserProfileResponse result = userProfileService.updateVerificationStatus(userId, VerificationStatus.VERIFIED);

        assertNotNull(result);
        assertEquals(response, result);
        assertEquals(VerificationStatus.VERIFIED, profile.getVerificationStatus());
        verify(userProfileRepository).save(profile);
    }

    @Test
    @DisplayName("uploadAvatar should upload image and save avatar url when profile exists")
    void uploadAvatar_whenProfileExists_shouldUploadAndSave() throws IOException {
        Long userId = 1L;
        MultipartFile file = mock(MultipartFile.class);
        UserProfile profile = new UserProfile();
        String expectedUrl = "http://cloudinary.com/avatar.jpg";

        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(imageService.uploadImage(file)).thenReturn(expectedUrl);

        userProfileService.uploadAvatar(userId, file);

        assertEquals(expectedUrl, profile.getAvatarUrl());
        verify(userProfileRepository).save(profile);
    }

    @Test
    @DisplayName("uploadAvatar should throw ProfileNotFoundException when profile does not exist")
    void uploadAvatar_whenProfileDoesNotExist_shouldThrowProfileNotFoundException() {
        Long userId = 1L;
        MultipartFile file = mock(MultipartFile.class);

        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(ProfileNotFoundException.class, () -> userProfileService.uploadAvatar(userId, file));
        verifyNoInteractions(imageService);
    }

    @Test
    @DisplayName("uploadAvatar should throw ImageUploadException when image upload fails with IOException")
    void uploadAvatar_whenUploadFails_shouldThrowImageUploadException() throws IOException {
        Long userId = 1L;
        MultipartFile file = mock(MultipartFile.class);
        UserProfile profile = new UserProfile();

        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(imageService.uploadImage(file)).thenThrow(new IOException("Connection failed"));

        assertThrows(ImageUploadException.class, () -> userProfileService.uploadAvatar(userId, file));
    }

    @Test
    @DisplayName("getPendingProfiles should return matching profiles page")
    void getPendingProfiles_shouldReturnPage() {
        UserProfile profile = new UserProfile();
        Page<UserProfile> profilesPage = new PageImpl<>(List.of(profile));
        Pageable pageable = Pageable.unpaged();

        when(userProfileRepository.findAllByVerificationStatus(VerificationStatus.PENDING, pageable)).thenReturn(profilesPage);
        PendingProfileResponse pendingResponse = mock(PendingProfileResponse.class);
        when(userProfileMapper.toPendingProfileResponse(profile)).thenReturn(pendingResponse);

        Page<PendingProfileResponse> result = userProfileService.getPendingProfiles(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(pendingResponse, result.getContent().getFirst());
    }

    @Test
    @DisplayName("validateUserCanPlaceOffers should do nothing when profile is fully verified and complete")
    void validateUserCanPlaceOffers_whenProfileComplete_shouldDoNothing() {
        Long userId = 1L;
        UserProfile profile = new UserProfile();
        profile.setPhoneNumber("+380991112233");
        profile.setBirthDate(LocalDate.now().minusYears(20));
        profile.setVerificationStatus(VerificationStatus.VERIFIED);

        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));

        assertDoesNotThrow(() -> userProfileService.validateUserCanPlaceOffers(userId));
    }

    @Test
    @DisplayName("validateUserCanPlaceOffers should throw ProfileIncompleteException when phone number is missing")
    void validateUserCanPlaceOffers_whenPhoneMissing_shouldThrowException() {
        Long userId = 1L;
        UserProfile profile = new UserProfile();
        profile.setPhoneNumber(null);
        profile.setBirthDate(LocalDate.now().minusYears(20));
        profile.setVerificationStatus(VerificationStatus.VERIFIED);

        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));

        assertThrows(ProfileIncompleteException.class, () -> userProfileService.validateUserCanPlaceOffers(userId));
    }

    @Test
    @DisplayName("validateUserCanPlaceOffers should throw ProfileIncompleteException when birth date is missing")
    void validateUserCanPlaceOffers_whenBirthDateMissing_shouldThrowException() {
        Long userId = 1L;
        UserProfile profile = new UserProfile();
        profile.setPhoneNumber("+380991112233");
        profile.setBirthDate(null);
        profile.setVerificationStatus(VerificationStatus.VERIFIED);

        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));

        assertThrows(ProfileIncompleteException.class, () -> userProfileService.validateUserCanPlaceOffers(userId));
    }

    @Test
    @DisplayName("validateUserCanPlaceOffers should throw ProfileIncompleteException when not verified")
    void validateUserCanPlaceOffers_whenNotVerified_shouldThrowException() {
        Long userId = 1L;
        UserProfile profile = new UserProfile();
        profile.setPhoneNumber("+380991112233");
        profile.setBirthDate(LocalDate.now().minusYears(20));
        profile.setVerificationStatus(VerificationStatus.UNVERIFIED);

        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));

        assertThrows(ProfileIncompleteException.class, () -> userProfileService.validateUserCanPlaceOffers(userId));
    }
}
