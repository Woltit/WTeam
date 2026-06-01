package com.wteam.backend.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторій для роботи з сутностями {@link Category}.
 * <p>
 * Забезпечує доступ до даних категорій товарів у базі даних.
 * </p>
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findBySlug(String slug);
}
