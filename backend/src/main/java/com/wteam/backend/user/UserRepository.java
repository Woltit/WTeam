package com.wteam.backend.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторій для роботи з сутностями {@link User}.
 * <p>
 * Забезпечує низькорівневий доступ до таблиці {@code users} у базі даних PostgreSQL.
 * Надає стандартні методи CRUD завдяки розширенню {@link JpaRepository}, а також кастомні методи пошуку.
 * </p>
 *
 * @see JpaRepository
 * @see User
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Повертає сторінку (Page) користувачів із заздалегідь завантаженими профілями.
     * <p>
     * Використання {@link EntityGraph} змушує Hibernate згенерувати SQL-запит із оператором {@code LEFT JOIN},
     * що дозволяє завантажити дані користувача та його {@code userProfile} за один запит до бази даних.
     * Це повністю вирішує проблему "N+1" для зв'язку {@link jakarta.persistence.FetchType#LAZY}.
     * </p>
     *
     * @param pageable об'єкт конфігурації пагінації та сортування (сторінка, розмір, напрямок сортування).
     * @return {@link Page} із сутностями {@link User}, у яких поле профілю вже ініціалізовано.
     */
    @EntityGraph(attributePaths = {"userProfile"}) // Виправлено з "profile" на "userProfile"
    Page<User> findAllWithProfile(Pageable pageable);

    /**
     * Виконує пошук користувача за його електронною поштою (email).
     * <p>
     * Метод використовується під час автентифікації користувача та перевірки JWT-токенів.
     * </p>
     *
     * @param email електронна пошта для пошуку.
     * @return {@link Optional}, що містить знайденого користувача, або порожній об'єкт, якщо користувача не знайдено.
     */
    Optional<User> findByEmail(String email);

    /**
     * Перевіряє, чи існує в системі користувач із вказаним email.
     * <p>
     * Використовується під час реєстрації нового користувача, щоб запобігти дублюванню
     * унікальних логінів та уникнути помилок обмеження унікальності (Unique Constraint) на рівні БД.
     * </p>
     *
     * @param email електронна пошта для перевірки.
     * @return {@code true}, якщо користувач із таким email вже зареєстрований; {@code false} в іншому випадку.
     */
    boolean existsByEmail(String email);
}
