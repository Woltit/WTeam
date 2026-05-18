package com.wteam.backend.user.dto;

import com.wteam.backend.common.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRoleUpdateRequest(
    @NotNull(message = "Role cannot be null")
    Role role
) {}
