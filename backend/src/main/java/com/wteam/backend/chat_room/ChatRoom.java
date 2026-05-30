package com.wteam.backend.chat_room;

import com.wteam.backend.booking.Booking;
import com.wteam.backend.common.entity.BaseEntityPart;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Сутність, що представляє кімнату чату між власником та орендарем.
 * Чат прив'язаний до конкретного бронювання.
 *
 * @see Booking
 * @see com.wteam.backend.message.Message
 */
@Entity
@Table(name = "chat_rooms")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class ChatRoom extends BaseEntityPart {
    /**
     * Унікальний ідентифікатор кімнати чату.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_rooms_gen")
    @SequenceGenerator(name = "chat_rooms_gen", sequenceName = "chat_rooms_id_seq", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    /**
     * Бронювання, до якого відноситься цей чат.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", referencedColumnName = "id", unique = true)
    private Booking booking;
}
