package com.wteam.backend.booking;

import com.wteam.backend.common.entity.BaseEntityFull;
import com.wteam.backend.common.enums.BookingStatus;
import com.wteam.backend.item.Item;
import com.wteam.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Сутність, що представляє бронювання товару користувачем.
 * Керує процесом оренди, датами та фінансовими розрахунками.
 *
 * @see Item
 * @see User
 * @see com.wteam.backend.common.enums.BookingStatus
 */
@Entity
@Table(name = "bookings")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class Booking extends BaseEntityFull {
    /**
     * Унікальний ідентифікатор бронювання.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bookings_gen")
    @SequenceGenerator(name = "bookings_gen", sequenceName = "bookings_id_seq", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    /**
     * Товар, який бронюється.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false)
    private Item item;

    /**
     * Користувач (орендар), який здійснює бронювання.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "renter_id", referencedColumnName = "id", nullable = false)
    private User renter;

    /**
     * Дата початку оренди.
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * Дата закінчення оренди.
     */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /**
     * Загальна вартість оренди.
     */
    @Column(name = "total_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalPrice;

    /**
     * Загальна сума застави.
     */
    @Column(name = "deposit_total", precision = 10, scale = 2, nullable = false)
    private BigDecimal depositTotal;

    /**
     * Знімок ціни за день на момент бронювання.
     */
    @Column(name = "price_per_day_snapshot", precision = 10, scale = 2, nullable = false)
    private BigDecimal pricePerDaySnapshot;

    /**
     * Поточний статус бронювання.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    /**
     * Причина скасування (якщо бронювання скасовано).
     */
    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;
}
