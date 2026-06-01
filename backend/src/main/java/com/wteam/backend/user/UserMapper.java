package com.wteam.backend.user;

import com.wteam.backend.user.dto.UserResponse;
import com.wteam.backend.user_profile.UserProfile;
import com.wteam.backend.user_profile.UserProfileMapper;
import com.wteam.backend.user_profile.dto.UserProfileRequest;
import com.wteam.backend.user_profile.dto.UserProfileResponse;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class UserMapper {
    private final UserProfileMapper userProfileMapper;


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
                userProfile != null ? userProfileMapper.toProfileResponse(userProfile) : null,
                user.getCreatedAt()
        );
    }
}
