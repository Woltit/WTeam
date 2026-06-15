package com.wteam.backend.user_profile;

import com.wteam.backend.common.enums.VerificationStatus;
import com.wteam.backend.security.annotation.CurrentUser;
import com.wteam.backend.security.dto.UserPrincipalDto;
import com.wteam.backend.user_profile.dto.PendingProfileResponse;
import com.wteam.backend.user_profile.dto.PublicProfileResponse;
import com.wteam.backend.user_profile.dto.UserProfileRequest;
import com.wteam.backend.user_profile.dto.UserProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileResponse> getMyProfile(@CurrentUser UserPrincipalDto user) {
        return ResponseEntity.ok(userProfileService.getProfile(user.id()));
    }

    @GetMapping("/public/{userId}")
    public ResponseEntity<PublicProfileResponse> getPublicProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userProfileService.getPublicProfile(userId));
    }

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @CurrentUser UserPrincipalDto user,
            @Valid @RequestBody UserProfileRequest request
    ) {
        return ResponseEntity.ok(userProfileService.updateProfile(user.id(), request));
    }

    @PatchMapping("/{id}/verification-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileResponse> updateVerificationStatus(
            @PathVariable Long id,
            @RequestParam VerificationStatus status
    ) {
        return ResponseEntity.ok(userProfileService.updateVerificationStatus(id, status));
    }

    @PostMapping("/avatar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> uploadAvatar(
            @CurrentUser UserPrincipalDto user,
            @RequestParam("file") MultipartFile file
    ) {
        userProfileService.uploadAvatar(user.id(), file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PendingProfileResponse>> getPendingProfiles(Pageable pageable) {
        return ResponseEntity.ok(userProfileService.getPendingProfiles(pageable));
    }

    @PostMapping("/submit-verification")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileResponse> submitForVerification(@CurrentUser UserPrincipalDto user) {
        return ResponseEntity.ok(userProfileService.submitForVerification(user.id()));
    }
}
