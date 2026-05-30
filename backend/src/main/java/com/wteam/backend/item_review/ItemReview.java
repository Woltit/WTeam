package com.wteam.backend.item_review;

import com.wteam.backend.booking.Booking;
import com.wteam.backend.common.entity.BaseEntityPart;
import com.wteam.backend.item.Item;
import com.wteam.backend.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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
    @UniqueConstraint(name = "uq_item_review_per_booking", columnNames = {"booking_id", "renter_id"})
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

    /**
     * Користувач (орендар), який залишив відгук.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "renter_id", referencedColumnName = "id")
    private User renter;

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
}
