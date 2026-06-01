package com.wteam.backend.user_profile;

import com.wteam.backend.common.enums.VerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
    Optional<UserProfile> findByUserId(Long userId);
    Page<UserProfile> findAllByVerificationStatus(VerificationStatus status, Pageable pageable);
}
