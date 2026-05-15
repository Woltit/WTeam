package com.wteam.backend.user;


import com.wteam.backend.common.enums.Role;
import com.wteam.backend.common.enums.VerificationStatus;
import com.wteam.backend.user.dto.UserRequest;
import com.wteam.backend.user.dto.UserResponse;
import com.wteam.backend.user_profile.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Invalid user");
        }

        UserProfile profile = user.getUserProfile();

        if (profile == null) {
            return new UserResponse(
                    user.getEmail(), user.getRole(),
                    null, null, null, null, null, null, null
            );
        }

        return new UserResponse(
                user.getEmail(),
                user.getRole(),
                profile.getLastName(),
                profile.getFirstName(),
                profile.getMiddleName(),
                profile.getBirthDate(),
                profile.getPhoneNumber(),
                profile.getBio(),
                profile.getVerificationStatus()
        );
    }

    public User toEntity(UserRequest userRequest) {
        if (userRequest == null) {
            throw new IllegalArgumentException("UserRequest is invalid");
        }

        User user = User.builder()
                .email(userRequest.email())
                .role(Role.USER)
                .build();

        UserProfile userProfile = UserProfile.builder()
                .lastName(userRequest.lastName())
                .firstName(userRequest.firstName())
                .verificationStatus(VerificationStatus.PENDING)
                .build();

        userProfile.setUser(user);
        user.setUserProfile(userProfile);

        return user;
    }
}
