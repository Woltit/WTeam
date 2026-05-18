package com.wteam.backend.user.dto;

import com.wteam.backend.common.enums.Role;
import com.wteam.backend.user_profile.dto.UserProfileResponse;

import java.time.Instant;

/**
 * DTO (Data Transfer Object) у вигляді Java-рекорду, що представляє вихідні дані користувача.
 * <p>
 * Використовується для повернення інформації про користувача та його вкладений персональний профіль
 * у відповідях на HTTP-запити (наприклад, після успішної автентифікації, реєстрації або при отриманні поточного профілю).
 * Забезпечує безпечне повернення даних без витоку конфіденційної інформації (такої як хеш пароля).
 * </p>
 *
 * @param id        Унікальний ідентифікатор користувача в системі, отриманий з базової сутності.
 * @param email     Електронна пошта користувача, яка також слугує його логіном.
 * @param role      Системна роль користувача, що визначає його рівень прав доступу на платформі.
 * @param profile   Вкладений об'єкт із детальною інформацією про персональний профіль користувача.
 * @param createdAt Точна дата та час створення облікового запису в системі.
 * * @see com.wteam.backend.common.enums.Role
 * @see com.wteam.backend.user_profile.dto.UserProfileResponse
 */
public record UserResponse(
        Long id,
        String email,
        Role role,
        UserProfileResponse profile,
        Instant createdAt
) {}