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

import static com.wteam.backend.common.constants.ValidationConstants.Booking.PRICE_PRECISION;
import static com.wteam.backend.common.constants.ValidationConstants.Booking.PRICE_SCALE;

@Entity
@Table(name = "bookings")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class Booking extends BaseEntityFull {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "renter_id", referencedColumnName = "id", nullable = false)
    private User renter;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "total_price", precision = PRICE_PRECISION, scale = PRICE_SCALE, nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "deposit_total", precision = PRICE_PRECISION, scale = PRICE_SCALE, nullable = false)
    private BigDecimal depositTotal;

    @Column(name = "price_per_day_snapshot", precision = PRICE_PRECISION, scale = PRICE_SCALE, nullable = false)
    private BigDecimal pricePerDaySnapshot;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;
}
