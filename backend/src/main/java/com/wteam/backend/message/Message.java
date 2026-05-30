package com.wteam.backend.message;

import com.wteam.backend.chat_room.ChatRoom;
import com.wteam.backend.common.entity.BaseEntityPart;
import com.wteam.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Сутність, що представляє повідомлення в чаті.
 *
 * @see ChatRoom
 * @see User
 */
@Entity
@Table(name = "messages")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class Message extends BaseEntityPart {
    /**
     * Унікальний ідентифікатор повідомлення.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "messages_gen")
    @SequenceGenerator(name = "messages_gen", sequenceName = "messages_id_seq", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    /**
     * Кімната чату, в якій надіслано повідомлення.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", referencedColumnName = "id", nullable = false)
    private ChatRoom chatRoom;

    /**
     * Відправник повідомлення.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "id", nullable = false)
    private User sender;

    /**
     * Текст повідомлення.
     */
    @Column(name = "message_text", columnDefinition = "TEXT", nullable = false)
    private String messageText;

    /**
     * Прапорець, що вказує, чи було повідомлення прочитане отримувачем.
     */
    @Column(name = "is_read")
    @Builder.Default
    private boolean isRead = false;
}
