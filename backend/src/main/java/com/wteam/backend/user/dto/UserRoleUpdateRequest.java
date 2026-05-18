package com.wteam.backend.user.dto;

import com.wteam.backend.common.enums.Role;
import jakarta.validation.constraints.NotNull;

/**
 * DTO (Data Transfer Object) у вигляді Java-рекорду, що представляє вхідні дані для оновлення системної ролі користувача.
 * <p>
 * Використовується в адміністративній панелі (наприклад, модераторами або адміністраторами)
 * для підвищення або зниження прав доступу конкретного акаунта в системі.
 * </p>
 *
 * @param role Нова системна роль, яку необхідно призначити користувачу. Поле є обов'язковим для заповнення.
 * @see com.wteam.backend.common.enums.Role
 */
public record UserRoleUpdateRequest(
        @NotNull(message = "Role cannot be null")
        Role role
) {}