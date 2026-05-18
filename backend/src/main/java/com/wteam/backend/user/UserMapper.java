package com.wteam.backend.user;


import com.wteam.backend.user.dto.UserResponse;
import com.wteam.backend.user_profile.UserProfile;
import com.wteam.backend.user_profile.dto.UserProfileRequest;
import com.wteam.backend.user_profile.dto.UserProfileResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }

        UserProfile userProfile = user.getUserProfile();

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                userProfile != null ? toProfileResponse(userProfile) : null,
                user.getCreatedAt()
        );
    }

    public UserProfileResponse toProfileResponse(UserProfile profile) {
        return new UserProfileResponse(
                profile.getLastName(),
                profile.getFirstName(),
                profile.getMiddleName(),
                profile.getBirthDate(),
                profile.getPhoneNumber(),
                profile.getBio(),
                profile.getVerificationStatus()
        );
    }

    public void updateProfileFromRequest(UserProfileRequest request, UserProfile profile) {
        profile.setLastName(request.lastName());
        profile.setFirstName(request.firstName());
        profile.setMiddleName(request.middleName());
        profile.setBirthDate(request.birthDate());
        profile.setPhoneNumber(request.phoneNumber());
        profile.setBio(request.bio());
    }
}
