package com.wteam.backend.user_profile.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

import static com.wteam.backend.common.constants.ValidationConstants.UserProfile.*;

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
