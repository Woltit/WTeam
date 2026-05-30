package com.wteam.backend.user_profile.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

import static com.wteam.backend.common.constants.ValidationConstants.UserProfile.*;

/**
 * DTO (Data Transfer Object) у вигляді Java-рекорду, що представляє вхідні дані для оновлення персонального профілю.
 * <p>
 * Використовується, коли користувач заповнює або редагує свої розширені дані в особистому кабінеті
 * для проходження модерації та отримання статусу верифікації. Містить суворі правила валідації
 * для забезпечення цілісності даних перед збереженням у базу.
 * </p>
 *
 * @param lastName    Прізвище користувача. Обов'язкове поле. Максимальна довжина обмежена константою {@link com.wteam.backend.common.constants.ValidationConstants.UserProfile#NAME_MAX_LENGTH NAME_MAX_LENGTH}.
 * @param firstName   Ім'я користувача. Обов'язкове поле. Максимальна довжина обмежена константою {@link com.wteam.backend.common.constants.ValidationConstants.UserProfile#NAME_MAX_LENGTH NAME_MAX_LENGTH}.
 * @param middleName  По батькові користувача. Необов'язкове поле, але якщо передане — валідується на максимальну довжину {@link com.wteam.backend.common.constants.ValidationConstants.UserProfile#NAME_MAX_LENGTH NAME_MAX_LENGTH}.
 * @param birthDate   Дата народження користувача. Обов'язкове поле для верифікації. Анотація {@link Past} гарантує, що вказана дата є в минулому часі.
 * @param phoneNumber Номер телефону користувача. Обов'язкове поле. Перевіряється на відповідність міжнародному формату E.164 за допомогою регулярного виразу {@link com.wteam.backend.common.constants.ValidationConstants.UserProfile#PHONE_REGEX PHONE_REGEX} та максимальну довжину {@link com.wteam.backend.common.constants.ValidationConstants.UserProfile#PHONE_NUMBER_LENGTH PHONE_NUMBER_LENGTH}.
 * @param bio         Коротка інформація про себе (біографія). Необов'язкове поле довільного текстового формату. * @see com.wteam.backend.user_profile.UserProfile
 * @see com.wteam.backend.common.constants.ValidationConstants
 */
public record UserProfileRequest(
        @NotBlank(message = LAST_NAME_BLANK_MSG)
        @Size(max = NAME_MAX_LENGTH)
        String lastName,

        @NotBlank(message = FIRST_NAME_BLANK_MSG)
        @Size(max = NAME_MAX_LENGTH)
        String firstName,

        @Size(max = NAME_MAX_LENGTH)
        String middleName,

        @NotNull(message = BIRTH_DATE_NULL_MSG)
        @Past
        LocalDate birthDate,

        @NotBlank(message = PHONE_NUMBER_BLANK_MSG)
        @Size(max = PHONE_NUMBER_LENGTH)
        @Pattern(
                regexp = PHONE_REGEX,
                message = PHONE_REGEX_INVALID_MSG
        )
        String phoneNumber,

        String bio
) {}
