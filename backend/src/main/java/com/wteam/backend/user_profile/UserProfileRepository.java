package com.wteam.backend.user_profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторій для роботи з сутностями {@link UserProfile}.
 * <p>
 * Забезпечує низькорівневий доступ до таблиці {@code user_profiles} у базі даних PostgreSQL.
 * Надає повний набір стандартних методів CRUD (створення, читання, оновлення, видалення),
 * а також підтримку пагінації та сортування завдяки розширенню інтерфейсу {@link JpaRepository}.
 * </p>
 *
 * @see JpaRepository
 * @see UserProfile
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
