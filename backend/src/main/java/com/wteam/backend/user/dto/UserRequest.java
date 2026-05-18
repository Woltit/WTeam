package com.wteam.backend.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static com.wteam.backend.common.constants.ValidationConstants.User.*;
import static com.wteam.backend.common.constants.ValidationConstants.UserProfile.*;

/**
 * DTO (Data Transfer Object) у вигляді Java-рекорду, що представляє вхідні дані для реєстрації нового користувача.
 * <p>
 * Об'єднує в собі облікові дані акаунта (email, password) та первинні персональні дані (lastName, firstName),
 * які необхідні для одночасного створення користувача та його базового профілю в рамках стратегії швидкої реєстрації.
 * Усі обмеження та тексти помилок імпортуються зі спільного класу констант валідації.
 * </p>
 *
 * @param email     Електронна пошта користувача. Обов'язкове поле, яке перевіряється на відповідність
 * стандартному формату email та максимальну довжину
 * {@link com.wteam.backend.common.constants.ValidationConstants.User#EMAIL_MAX_LENGTH EMAIL_MAX_LENGTH}.
 * @param password  Пароль користувача у вихідному (нехешованому) вигляді. Обов'язкове поле. Валідується на
 * безпечну довжину в діапазоні від {@link com.wteam.backend.common.constants.ValidationConstants.User#PASSWORD_MIN_LENGTH PASSWORD_MIN_LENGTH}
 * до {@link com.wteam.backend.common.constants.ValidationConstants.User#PASSWORD_MAX_LENGTH PASSWORD_MAX_LENGTH}.
 * @param lastName  Прізвище користувача. Використовується для первинного заповнення картки профілю. Обов'язкове поле,
 * максимальна довжина якого обмежена константою
 * {@link com.wteam.backend.common.constants.ValidationConstants.UserProfile#NAME_MAX_LENGTH NAME_MAX_LENGTH}.
 * @param firstName Ім'я користувача. Використовується для первинного заповнення картки профілю. Обов'язкове поле,
 * максимальна довжина якого обмежена константою
 * {@link com.wteam.backend.common.constants.ValidationConstants.UserProfile#NAME_MAX_LENGTH NAME_MAX_LENGTH}.
 * * @see com.wteam.backend.common.constants.ValidationConstants
 */
public record UserRequest (
        @NotBlank(message = EMAIL_BLANK_MSG)
        @Email(message = EMAIL_INVALID_FORMAT)
        @Size(max = EMAIL_MAX_LENGTH)
        String email,

        @NotBlank(message = PASSWORD_BLANK_MSG)
        @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH,
            message = PASSWORD_INVALID_FORMAT)
        String password,

        @NotBlank(message = LAST_NAME_BLANK_MSG)
        @Size(max = NAME_MAX_LENGTH)
        String lastName,

        @NotBlank(message = FIRST_NAME_BLANK_MSG)
        @Size(max = NAME_MAX_LENGTH)
        String firstName
) {}
