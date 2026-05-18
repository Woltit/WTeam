package com.wteam.backend.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static com.wteam.backend.common.constants.ValidationConstants.User.*;
import static com.wteam.backend.common.constants.ValidationConstants.UserProfile.*;

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
