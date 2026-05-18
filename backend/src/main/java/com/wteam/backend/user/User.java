package com.wteam.backend.user;

import com.wteam.backend.common.entity.BaseEntity;
import com.wteam.backend.common.enums.Role;
import com.wteam.backend.user_profile.UserProfile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import static com.wteam.backend.common.constants.ValidationConstants.User.*;

/**
 * JPA-сутність, що представляє користувача в системі.
 * <p>
 * Цей клас відображає таблицю {@code users} у базі даних. Він відповідає за збереження
 * основних облікових даних користувача (email, пароль) та його системної ролі для
 * контролю доступу (авторизації).
 * </p>
 *
 * @see BaseEntity
 * @see UserProfile
 */
@Entity
@Table(name = "users")
@SuperBuilder
@NoArgsConstructor @AllArgsConstructor
@Setter @Getter
public class User extends BaseEntity {
    /**
     * Електронна пошта користувача.
     * <p>
     * Використовується як унікальний ідентифікатор (логін) під час автентифікації.
     * Обмеження максимальної довжини визначається константою {@link com.wteam.backend.common.constants.ValidationConstants.User#EMAIL_MAX_LENGTH EMAIL_MAX_LENGTH}.
     * </p>
     */
    @Column(name = "email", length = EMAIL_MAX_LENGTH, nullable = false, unique = true)
    private String email;

    /**
     * Хешований пароль користувача для входу в систему.
     * <p>
     * Поле може залишатися порожнім ({@code null}), буде реалізовано
     * авторизацію через сторонні сервіси (OAuth2 / Google / Apple ID).
     * Обмеження максимальної довжини визначається константою {@link com.wteam.backend.common.constants.ValidationConstants.User#PASSWORD_MAX_LENGTH PASSWORD_MAX_LENGTH}
     * </p>
     */
    @Column(name = "password", length = PASSWORD_MAX_LENGTH)
    private String password;

    /**
     * Системна роль користувача.
     * <p>
     * Визначає рівень прав доступу в додатку. Зберігається в базі даних у вигляді
     * рядка (String) для зручності читання та гнучкості при міграціях.
     * </p>
     *
     * @see Role
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, columnDefinition = "role")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private Role role;

    /**
     * Персональний профіль користувача.
     * <p>
     * Містить додаткову інформацію (ім'я, телефон, статус верифікації).
     * Зв'язок є двонаправленим, використовує ліниве завантаження ({@link FetchType#LAZY})
     * та каскадне керування ({@link CascadeType#ALL}), завдяки чому профіль автоматично
     * створюється, оновлюється або видаляється разом із користувачем.
     * </p>
     */
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private UserProfile userProfile;
}
