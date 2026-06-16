package com.wteam.backend.item_review;

import com.wteam.backend.booking.Booking;
import com.wteam.backend.common.entity.BaseEntityPart;
import com.wteam.backend.item.Item;
import com.wteam.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Сутність, що представляє відгук користувача про товар.
 * Створюється орендарем за результатами бронювання.
 *
 * @see Item
 * @see User
 * @see Booking
 */
@Entity
@Table(name = "item_reviews", uniqueConstraints = {
        @UniqueConstraint(name = "uq_item_review_per_booking", columnNames = {"booking_id", "reviewer_id"})
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class ItemReview extends BaseEntityPart {
    /**
     * Унікальний ідентифікатор відгуку.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_reviews_gen")
    @SequenceGenerator(name = "item_reviews_gen", sequenceName = "item_reviews_id_seq", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    /**
     * Товар, про який залишено відгук.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", referencedColumnName = "id")
    private User reviewer;

    /**
     * Бронювання, за результатами якого залишено відгук.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", referencedColumnName = "id")
    private Booking booking;

    /**
     * Рейтинг товару (1-5).
     */
    @Column(name = "rating", nullable = false)
    private short rating;

    /**
     * Текстовий коментар до відгуку.
     */
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    /**
     * Статус відгуку (PENDING / PUBLISHED).
     */
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private com.wteam.backend.common.enums.ReviewStatus status = com.wteam.backend.common.enums.ReviewStatus.PENDING;
}
