package com.wteam.backend.user_profile;

import com.wteam.backend.common.entity.BaseEntity;
import com.wteam.backend.common.enums.VerificationStatus;
import com.wteam.backend.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

import static com.wteam.backend.common.constants.ValidationConstants.UserProfile.*;

/**
 * JPA-сутність, що представляє персональний профіль користувача.
 * <p>
 * Цей клас відображає таблицю {@code user_profiles} у базі даних і містить
 * розширену інформацію про користувача (ПІБ, контакти, статус верифікації).
 * Сутність є власником зв'язку (owning side) у відносинах із {@link User}.
 * </p>
 *
 * @see BaseEntity
 * @see User
 */
@Entity
@Table(name = "user_profiles")
@SuperBuilder
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserProfile extends BaseEntity {

    /**
     * Посилання на головну сутність користувача, якому належить цей профіль.
     * <p>
     * Визначає зовнішній ключ {@code user_id} у таблиці профілів. Використовує
     * ліниве завантаження ({@link FetchType#LAZY}) для оптимізації SQL-запитів.
     * </p>
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    /**
     * Прізвище користувача.
     * <p>
     * Обов'язкове поле для заповнення. Максимальна довжина обмежена константою
     * {@link com.wteam.backend.common.constants.ValidationConstants.UserProfile#NAME_MAX_LENGTH NAME_MAX_LENGTH}.
     * </p>
     */
    @Column(name = "last_name" , length = NAME_MAX_LENGTH , nullable = false)
    private String lastName;

    /**
     * Ім'я користувача.
     * <p>
     * Обов'язкове поле для заповнення. Максимальна довжина обмежена константою
     * {@link com.wteam.backend.common.constants.ValidationConstants.UserProfile#NAME_MAX_LENGTH NAME_MAX_LENGTH}.
     * </p>
     */
    @Column(name = "first_name" , length = NAME_MAX_LENGTH, nullable = false)
    private String firstName;

    /**
     * По батькові користувача.
     * <p>
     * Необов'язкове поле (може бути {@code null}). Максимальна довжина обмежена константою
     * {@link com.wteam.backend.common.constants.ValidationConstants.UserProfile#NAME_MAX_LENGTH NAME_MAX_LENGTH}.
     * </p>
     */
    @Column(name = "middle_name", length = NAME_MAX_LENGTH)
    private String middleName;

    /**
     * Дата народження користувача.
     * <p>
     * Необов'язкове поле при швидкій реєстрації, але стає обов'язковим для верифікації профілю
     * з метою надання послуг оренди.
     * </p>
     */
    @Column(name = "birth_date")
    private LocalDate birthDate;

    /**
     * Номер телефону користувача.
     * <p>
     * Необов'язкове поле при швидкій реєстрації. Має міжнародний формат (включаючи префікс +).
     * Максимальна довжина обмежена константою
     * {@link com.wteam.backend.common.constants.ValidationConstants.UserProfile#PHONE_NUMBER_LENGTH PHONE_NUMBER_LENGTH}.
     * </p>
     */
    @Column(name = "phoneNumber", length = PHONE_NUMBER_LENGTH)
    private String phoneNumber;

    /**
     * Коротка біографія або додаткова інформація про користувача.
     * <p>
     * Необов'язкове текстове поле довільної довжини.
     * </p>
     */
    @Column(name = "bio")
    private String bio;

    /**
     * Статус верифікації профілю модератором.
     * <p>
     * Обов'язкове системне поле. Визначає рівень доступу користувача до створення оголошень.
     * Зберігається в БД у вигляді рядка (String).
     * </p>
     *
     * @see VerificationStatus
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, columnDefinition = "verification_status")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private VerificationStatus verificationStatus;
}
