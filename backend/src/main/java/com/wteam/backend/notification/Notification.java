package com.wteam.backend.notification;

import com.wteam.backend.booking.Booking;
import com.wteam.backend.common.entity.BaseEntityPart;
import com.wteam.backend.common.enums.NotificationChannel;
import com.wteam.backend.common.enums.NotificationType;
import com.wteam.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Сутність, що представляє системне сповіщення для користувача.
 *
 * @see User
 * @see Booking
 * @see com.wteam.backend.common.enums.NotificationType
 * @see com.wteam.backend.common.enums.NotificationChannel
 */
@Entity
@Table(name = "notifications")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class Notification extends BaseEntityPart {
    /**
     * Унікальний ідентифікатор сповіщення.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notifications_gen")
    @SequenceGenerator(name = "notifications_gen", sequenceName = "notifications_id_seq", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    /**
     * Користувач, якому адресоване сповіщення.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    /**
     * Тип сповіщення.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private NotificationType type;

    /**
     * Канал доставки сповіщення.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Builder.Default
    private NotificationChannel channel = NotificationChannel.IN_APP;

    /**
     * Заголовок сповіщення.
     */
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Текст сповіщення.
     */
    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    /**
     * Прапорець, що вказує, чи було сповіщення прочитане.
     */
    @Column(name = "is_read")
    @Builder.Default
    private boolean isRead = false;

    /**
     * Бронювання, до якого відноситься це сповіщення (якщо є).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_booking_id", referencedColumnName = "id")
    private Booking relatedBooking;
}
