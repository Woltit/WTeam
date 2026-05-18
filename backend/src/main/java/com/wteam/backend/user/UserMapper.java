package com.wteam.backend.user;

import com.wteam.backend.user.dto.UserResponse;
import com.wteam.backend.user_profile.UserProfile;
import com.wteam.backend.user_profile.dto.UserProfileRequest;
import com.wteam.backend.user_profile.dto.UserProfileResponse;
import org.springframework.stereotype.Component;

/**
 * Компонент-мапер для конвертації даних між сутностями модуля користувачів та відповідними DTO.
 * <p>
 * Цей клас керований контейнером Spring ({@link Component}) і забезпечує логіку трансформації
 * об'єктів для ізоляції внутрішньої моделі бази даних від зовнішнього API (фронтенду).
 * </p>
 *
 * @see User
 * @see UserResponse
 * @see UserProfile
 * @see UserProfileResponse
 * @see UserProfileRequest
 */
@Component
public class UserMapper {

    /**
     * Конвертує JPA-сутність {@link User} у вихідний об'єкт відповіді {@link UserResponse}.
     * <p>
     * Метод автоматично перевіряє наявність пов'язаного профілю користувача. Якщо профіль існує,
     * він також каскадно конвертується у формат {@link UserProfileResponse}. Якщо профіль відсутній
     * (наприклад, на проміжних етапах реєстрації), у відповідне поле буде записано {@code null}.
     * </p>
     *
     * @param user сутність користувача, яку необхідно конвертувати. Не може бути {@code null}.
     * @return заповнений об'єкт {@link UserResponse} для відправки клієнту.
     * @throws IllegalArgumentException якщо переданий об'єкт користувача є {@code null}.
     */
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
                profile.getVerificationStatus()
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
