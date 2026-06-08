package com.wteam.backend.security.oauth2;

import com.wteam.backend.common.enums.AuthProvider;
import com.wteam.backend.common.enums.Role;
import com.wteam.backend.common.enums.VerificationStatus;
import com.wteam.backend.user.User;
import com.wteam.backend.user.UserRepository;
import com.wteam.backend.user_profile.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        String email = oidcUser.getEmail();
        userRepository.findByEmail(email)
                .orElseGet(() -> registerNewUser(oidcUser, userRequest));

        return oidcUser;
    }

    private User registerNewUser(OidcUser oidcUser, OidcUserRequest userRequest) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());

        User user = User.builder()
                .email(oidcUser.getEmail())
                .password(null)
                .authProvider(provider)
                .isActive(true)
                .role(Role.USER)
                .build();

        UserProfile profile = UserProfile.builder()
                .user(user)
                .lastName(oidcUser.getFamilyName() != null ? oidcUser.getFamilyName() : "")
                .firstName(oidcUser.getGivenName() != null ? oidcUser.getGivenName() : "User")
                .avatarUrl(oidcUser.getPicture())
                .verificationStatus(VerificationStatus.UNVERIFIED)
                .build();

        user.setUserProfile(profile);
        return userRepository.save(user);
    }
}
