package com.wteam.backend.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequest(
    @NotNull(message = "Email cannot be null")
    @Email(message = "Invalid email")
    String email,

    @NotNull(message = "Password cannot be null")
    @Size(min = 8, message = "Password cannot be less than 8 characters")
    String password,

    @NotNull(message = "LastName cannot be null")
    String lastName,

    @NotNull(message = "FirstName cannot be null")
    String firstName
) {}
