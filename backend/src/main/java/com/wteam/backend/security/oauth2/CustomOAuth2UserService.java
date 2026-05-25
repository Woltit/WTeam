package com.wteam.backend.security.oauth2;

import com.wteam.backend.common.enums.AuthProvider;
import com.wteam.backend.common.enums.Role;
import com.wteam.backend.common.enums.VerificationStatus;
import com.wteam.backend.security.SecurityUser;
import com.wteam.backend.user.User;
import com.wteam.backend.user.UserRepository;
import com.wteam.backend.user_profile.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2UserInfo userInfo = OAuth2UserInfo.fromProvider(
                userRequest.getClientRegistration().getRegistrationId(),
                oAuth2User.getAttributes()
        );

        User user = userRepository.findByEmail(userInfo.email())
                .orElseGet(() -> registerNewOAuth2User(userInfo));

        return SecurityUser.create(user, oAuth2User.getAttributes());
    }

    private User registerNewOAuth2User(OAuth2UserInfo userInfo) {
        User user = User.builder()
                .email(userInfo.email())
                .password(null)
                .authProvider(userInfo.provider())
                .isActive(true)
                .role(Role.USER)
                .build();

        UserProfile profile = UserProfile.builder()
                .user(user)
                .lastName(userInfo.lastName())
                .firstName(userInfo.firstName())
                .avatarUrl(userInfo.avatarUrl())
                .verificationStatus(VerificationStatus.UNVERIFIED)
                .build();

        user.setUserProfile(profile);

        return userRepository.save(user);
    }
}
