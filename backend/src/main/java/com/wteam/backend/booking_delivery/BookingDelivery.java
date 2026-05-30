package com.wteam.backend.booking_delivery;

import com.wteam.backend.booking.Booking;
import com.wteam.backend.common.entity.BaseEntityFull;
import com.wteam.backend.common.enums.DeliveryMethod;
import com.wteam.backend.common.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

/**
 * Сутність, що представляє деталі доставки бронювання.
 * Зберігає інформацію про метод доставки, адресу та статус транспортування.
 *
 * @see Booking
 * @see com.wteam.backend.common.enums.DeliveryMethod
 * @see com.wteam.backend.common.enums.DeliveryStatus
 */
@Entity
@Table(name = "booking_deliveries")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class BookingDelivery extends BaseEntityFull {
    /**
     * Унікальний ідентифікатор доставки.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "booking_deliveries_gen")
    @SequenceGenerator(name = "booking_deliveries_gen", sequenceName = "booking_deliveries_id_seq", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    /**
     * Бронювання, до якого відноситься ця доставка.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", referencedColumnName = "id", unique = true, nullable = false)
    private Booking booking;

    /**
     * Метод доставки (наприклад, самовивіз або кур'єр).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private DeliveryMethod method;

    /**
     * Номер накладної або трекінг-номер відправлення.
     */
    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    /**
     * Адреса доставки.
     */
    @Column(name = "delivery_address", columnDefinition = "TEXT")
    private String deliveryAddress;

    /**
     * Очікувана дата доставки.
     */
    @Column(name = "estimated_delivery_date")
    private LocalDate estimatedDeliveryDate;

    /**
     * Поточний статус доставки.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private DeliveryStatus status;
}
