package com.wteam.backend.dispute;

import com.wteam.backend.booking.Booking;
import com.wteam.backend.common.entity.BaseEntityFull;
import com.wteam.backend.common.enums.DisputeReason;
import com.wteam.backend.common.enums.DisputeStatus;
import com.wteam.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Сутність, що представляє суперечку (диспут), відкриту користувачем.
 * Стосується конкретного бронювання та потребує втручання модератора.
 *
 * @see Booking
 * @see User
 * @see com.wteam.backend.common.enums.DisputeReason
 * @see com.wteam.backend.common.enums.DisputeStatus
 */
@Entity
@Table(name = "disputes", uniqueConstraints = {
    @UniqueConstraint(name = "uq_one_dispute_per_booking", columnNames = {"booking_id"})
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class Dispute extends BaseEntityFull {
    /**
     * Унікальний ідентифікатор суперечки.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "disputes_gen")
    @SequenceGenerator(name = "disputes_gen", sequenceName = "disputes_id_seq", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    /**
     * Бронювання, за яким відкрита суперечка.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", referencedColumnName = "id", nullable = false)
    private Booking booking;

    /**
     * Користувач, який ініціював суперечку.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", referencedColumnName = "id", nullable = false)
    private User initiator;

    /**
     * Причина відкриття суперечки.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private DisputeReason reason;

    /**
     * Детальний опис претензії.
     */
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    /**
     * Текст рішення за суперечкою.
     */
    @Column(name = "resolution", columnDefinition = "TEXT")
    private String resolution;

    /**
     * Модератор або адміністратор, який виніс рішення.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by", referencedColumnName = "id")
    private User resolvedBy;

    /**
     * Поточний статус розгляду суперечки.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Builder.Default
    private DisputeStatus status = DisputeStatus.OPEN;
}
