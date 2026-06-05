package com.wteam.backend.item;

import com.wteam.backend.category.Category;
import com.wteam.backend.common.enums.RentingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Репозиторій для роботи з сутностями {@link Item}.
 * <p>
 * Забезпечує доступ до даних товарів у базі даних.
 * </p>
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findAllByOwnerId(Long ownerId, Pageable pageable);

    Page<Item> findAllByCityAndStatus(String city, RentingStatus status, Pageable pageable);

    Page<Item> findAllByCategoryAndStatusAndIsVerified(Category category, RentingStatus status, boolean isVerified, Pageable pageable);

    @Query(
    """
    SELECT i FROM Item i
    WHERE i.status = :status AND i.isVerified = true
      AND (LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
    Page<Item> searchActiveByKeyword(
            @Param("keyword") String keyword,
            @Param("status") RentingStatus status,
            Pageable pageable
    );

    Page<Item> findAllByStatusAndIsVerifiedTrue(RentingStatus status, Pageable pageable);

    boolean existsByIdAndOwnerId(Long id, Long ownerId);
}
