package com.wteam.backend.auth.dto;

import com.wteam.backend.common.validation.PasswordsMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static com.wteam.backend.common.validation.ValidationConstants.Constraints.*;

/**
 * Об'єкт передачі даних (DTO) для запиту на реєстрацію користувача.
 *
 * @param email електронна пошта користувача
 * @param password пароль користувача
 * @param checkPassword підтвердження пароля
 */
@PasswordsMatch(
        originalPassword = "password",
        checkPassword = "checkPassword",
        message = "Passwords do not match"
)
public record RegisterRequest(
        @NotBlank(message = "Email is essential")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is essential")
        @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH, message = PASSWORD_INVALID_MSG)
        String password,

        @NotBlank(message = "Password confirmation is essential")
        @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH, message = PASSWORD_INVALID_MSG)
        String checkPassword
) {}
