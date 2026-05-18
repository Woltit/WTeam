package com.wteam.backend.security;

import com.wteam.backend.common.enums.Role;
import com.wteam.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Адаптер (клас-обгортка) над сутністю {@link User} для інтеграції зі Spring Security.
 * <p>
 * Реалізує інтерфейс {@link UserDetails}, дозволяючи фреймворку безпеки працювати
 * з користувачем системи, не зв'язуючи наряму JPA-сутність бази даних з інфраструктурою безпеки.
 * Поле {@code user} позначене як {@code transient} для запобігання помилкам під час можливої серіалізації.
 * </p>
 *
 * @see UserDetails
 * @see User
 */
@RequiredArgsConstructor
public class SecurityUser implements UserDetails {
    private final transient User user;

    /**
     * Повертає унікальний ідентифікатор користувача з бази даних.
     *
     * @return унікальний ID користувача.
     */
    public Long getId() {
        return user.getId();
    }

    /**
     * Повертає чисту системну роль користувача у вигляді Enum.
     *
     * @return {@link Role} користувача.
     */
    public Role getRole() {
        return user.getRole();
    }

    /**
     * Повертає колекцію прав (granted authorities) користувача.
     * <p>
     * Конвертує внутрішню роль системи у формат, який очікує Spring Security,
     * додаючи обов'язковий префікс {@code ROLE_} (наприклад, {@code ROLE_USER}, {@code ROLE_ADMIN}).
     * Це дозволяє використовувати стандартні перевірки на кшталт {@code @PreAuthorize("hasRole('ADMIN')")}.
     * </p>
     *
     * @return колекція, що містить об'єкт {@link SimpleGrantedAuthority} з роллю користувача.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    /**
     * Повертає хешований пароль користувача.
     *
     * @return рядок із хешем пароля або {@code null}, якщо користувач автентифікований через OAuth2.
     */
    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    /**
     * Повертає унікальне ім'я користувача (username), яке використовується для входу.
     * У рамках цієї системи цим ім'ям є електронна пошта.
     *
     * @return email користувача.
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Перевіряє, чи не закінчився термін дії облікового запису.
     *
     * @return завжди {@code true} (обмеження за часом життя акаунта відсутні).
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Перевіряє, чи не заблокований обліковий запис.
     *
     * @return always {@code true} (система блокування користувачів поки відсутня).
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Перевіряє, чи не застаріли облікові дані (пароль) користувача.
     *
     * @return завжди {@code true} (примусова регулярна зміна пароля відсутня).
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Перевіряє, чи увімкнений (активний) обліковий запис користувача.
     *
     * @return завжди {@code true} (всі створені користувачі активні за замовчуванням).
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
