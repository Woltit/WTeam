package com.wteam.backend.user_profile.dto;

import com.wteam.backend.common.enums.VerificationStatus;
import jakarta.validation.constraints.NotNull;

/**
 * DTO (Data Transfer Object) у вигляді Java-рекорду, що представляє запит на зміну статусу верифікації профілю користувача.
 * <p>
 * Використовується в адміністративній панелі модераторами платформи для затвердження або відхилення
 * первинних документів користувача. Дозволяє передати фінальне рішення разом із текстовим обґрунтуванням.
 * </p>
 *
 * @param verificationStatus Новий статус верифікації, який призначається профілю. Поле є обов'язковим для заповнення.
 * @param comment            Супровідний коментар або замітка модератора. Обов'язково заповнюється у разі відхилення
 * запиту (статус {@link com.wteam.backend.common.enums.VerificationStatus#REJECTED REJECTED}) для пояснення причин користувачу.
 * * @see com.wteam.backend.user_profile.UserProfile
 * @see com.wteam.backend.common.enums.VerificationStatus
 */
public record UserProfileVerificationRequest(
        @NotNull(message = "Verification status cannot be null")
        VerificationStatus verificationStatus,
        String comment
) {}
