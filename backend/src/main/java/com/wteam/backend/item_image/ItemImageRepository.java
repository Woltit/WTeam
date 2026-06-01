package com.wteam.backend.item_image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторій для роботи з сутностями {@link ItemImage}.
 * <p>
 * Забезпечує доступ до даних про зображення товарів у базі даних.
 * </p>
 */
@Repository
public interface ItemImageRepository extends JpaRepository<ItemImage, Long> {
}
