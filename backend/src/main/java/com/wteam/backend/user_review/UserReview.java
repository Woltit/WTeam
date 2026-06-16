package com.wteam.backend.user_review;

import com.wteam.backend.booking.Booking;
import com.wteam.backend.common.entity.BaseEntityFull;
import com.wteam.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Сутність, що представляє відгук користувача про іншого користувача.
 * Створюється за результатами бронювання.
 *
 * @see User
 * @see Booking
 */
@Entity
@Table(name = "user_reviews", uniqueConstraints = {
        @UniqueConstraint(name = "uq_user_review_per_booking", columnNames = {"booking_id", "reviewer_id", "target_user_id"})
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class UserReview extends BaseEntityFull {
    /**
     * Унікальний ідентифікатор відгуку.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_reviews_gen")
    @SequenceGenerator(name = "user_reviews_gen", sequenceName = "user_reviews_id_seq", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id", referencedColumnName = "id", nullable = false)
    private User targetUser;

    /**
     * Користувач, який залишає відгук.
     */
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
     * Роль цільового користувача.
     */
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "target_role", nullable = false)
    private com.wteam.backend.common.enums.TargetRole targetRole;

    /**
     * Рейтинг (1-5).
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
