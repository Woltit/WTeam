package com.wteam.backend.user_profile;

import com.wteam.backend.user_profile.dto.PendingProfileResponse;
import com.wteam.backend.user_profile.dto.PublicProfileResponse;
import com.wteam.backend.user_profile.dto.UserProfileRequest;
import com.wteam.backend.user_profile.dto.UserProfileResponse;
import org.springframework.stereotype.Component;

@Component
public class UserProfileMapper {
    /**
     * Конвертує сутність {@link UserProfile} у об'єкт відповіді {@link UserProfileResponse}.
     *
     * @param profile сутність профілю користувача для конвертації.
     * @return об'єкт {@link UserProfileResponse} з публічними даними профілю.
     */
    public UserProfileResponse toProfileResponse(UserProfile profile) {
        return new UserProfileResponse(
                profile.getLastName(),
                profile.getFirstName(),
                profile.getMiddleName(),
                profile.getBirthDate(),
                profile.getPhoneNumber(),
                profile.getBio(),
                profile.getAvatarUrl(),
                profile.getVerificationStatus(),
                profile.getRenterTrustScore(),
                profile.getOwnerTrustScore(),
                profile.getTotalSuccessfulRents()
        );
    }

    public PendingProfileResponse toPendingProfileResponse(UserProfile profile) {
        return new PendingProfileResponse(
                profile.getUser().getId(),
                profile.getUser().getEmail(),
                profile.getLastName(),
                profile.getFirstName(),
                profile.getMiddleName(),
                profile.getBirthDate(),
                profile.getPhoneNumber(),
                profile.getVerificationStatus()
        );
    }

    public PublicProfileResponse toPublicProfileResponse(UserProfile profile) {
        return new PublicProfileResponse(
                profile.getLastName(),
                profile.getFirstName(),
                profile.getMiddleName(),
                profile.getBio(),
                profile.getAvatarUrl(),
                profile.getRenterTrustScore(),
                profile.getOwnerTrustScore(),
                profile.getTotalSuccessfulRents()
        );
    }

    /**
     * Оновлює існуючу сутність {@link UserProfile} даними з вхідного запиту {@link UserProfileRequest}.
     * <p>
     * Метод модифікує переданий об'єкт профілю на місці (in-place) шляхом виклику відповідних сетерів.
     * Він не створює новий об'єкт профілю, що дозволяє Hibernate коректно відстежувати зміни (dirty checking)
     * і оновлювати лише потрібні стовпці в базі даних під час транзакції.
     * </p>
     *
     * @param request DTO запиту, що містить нові персональні дані від користувача.
     * @param profile існуюча JPA-сутність профілю, яку необхідно оновити.
     */
    public void updateProfileFromRequest(UserProfileRequest request, UserProfile profile) {
        profile.setLastName(request.lastName());
        profile.setFirstName(request.firstName());
        profile.setMiddleName(request.middleName());
        profile.setBirthDate(request.birthDate());
        profile.setPhoneNumber(request.phoneNumber());
        profile.setBio(request.bio());
    }
}
