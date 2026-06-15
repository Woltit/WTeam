package com.wteam.backend.item;

import com.wteam.backend.admin.dto.CategoryStatDto;
import com.wteam.backend.category.Category;
import com.wteam.backend.common.enums.RentingStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторій для роботи з сутностями {@link Item}.
 * <p>
 * Забезпечує доступ до даних товарів у базі даних.
 * </p>
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @EntityGraph(attributePaths = {"category", "owner"})
    Page<Item> findAllByOwnerId(Long ownerId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Item i WHERE i.id = :id")
    Optional<Item> findByIdForUpdate(@Param("id") Long id);

    @EntityGraph(attributePaths = {"category", "owner"})
    Page<Item> findAllByCityAndStatus(String city, RentingStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "owner"})
    Page<Item> findAllByCategoryAndStatusAndIsVerifiedTrue(Category category, RentingStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "owner"})
    @Query(
        value = """
        SELECT i FROM Item i
        WHERE i.status = :status AND i.isVerified = true
          AND (LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """,
        countQuery = """
        SELECT COUNT(i) FROM Item i
        WHERE i.status = :status AND i.isVerified = true
          AND (LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """
    )
    Page<Item> searchActiveByKeyword(
            @Param("keyword") String keyword,
            @Param("status") RentingStatus status,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"category", "owner"})
    Page<Item> findAllByStatusAndIsVerifiedTrue(RentingStatus status, Pageable pageable);

    boolean existsByIdAndOwnerId(Long id, Long ownerId);

    @Query("""
        SELECT new com.wteam.backend.admin.dto.CategoryStatDto(i.category.name, COUNT(i))
        FROM Item i
        GROUP BY i.category.id, i.category.name
        ORDER BY COUNT(i) DESC
    """)
    List<CategoryStatDto> findTopCategoriesByItemCount(Pageable pageable);
}
