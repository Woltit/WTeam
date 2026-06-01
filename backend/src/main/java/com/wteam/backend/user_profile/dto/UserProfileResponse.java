package com.wteam.backend.user_profile.dto;

import com.wteam.backend.common.enums.VerificationStatus;
import com.wteam.backend.user_profile.UserProfile;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO (Data Transfer Object) у вигляді Java-рекорду, що представляє вихідні дані персонального профілю користувача.
 * <p>
 * Використовується для повернення розширеної інформації про користувача (ПІБ, контакти, статус верифікації)
 * у відповідях на HTTP-запити. На відміну від JPA-сутності, цей об'єкт є незмінним (immutable)
 * і містить лише ті дані, які безпечно та необхідно передавати на клієнтську сторону (фронтенд).
 * </p>
 *
 * @param lastName           Прізвище користувача.
 * @param firstName          Ім'я користувача.
 * @param middleName         По батькові користувача (може бути {@code null}, якщо не вказано).
 * @param birthDate          Дата народження користувача (може бути {@code null} на етапі швидкої реєстрації).
 * @param phoneNumber        Номер телефону користувача в міжнародному форматі (може бути {@code null} на етапі швидкої реєстрації).
 * @param bio                Коротка текстова біографія або інформація про себе (може бути {@code null}).
 * @param verificationStatus Поточний статус верифікації профілю модератором платформи, який визначає рівень доступу до функцій оренди. * @see com.wteam.backend.user_profile.UserProfile
 * @see com.wteam.backend.common.enums.VerificationStatus
 */
public record UserProfileResponse(
        String lastName,
        String firstName,
        String middleName,
        LocalDate birthDate,
        String phoneNumber,
        String bio,
        String avatarUrl,
        VerificationStatus verificationStatus,
        BigDecimal renterTrustScore,
        BigDecimal ownerTrustScore,
        Integer totalSuccessfulRents
) {}
