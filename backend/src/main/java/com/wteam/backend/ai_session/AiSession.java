package com.wteam.backend.ai_session;

import com.wteam.backend.common.entity.BaseEntityPart;
import com.wteam.backend.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Сутність, що представляє сесію взаємодії користувача з ШІ-помічником.
 * Зберігає запит користувача, відповідь ШІ та список рекомендованих товарів.
 *
 * @see User
 * @see com.wteam.backend.item.Item
 */
@Entity
@Table(name = "ai_sessions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class AiSession extends BaseEntityPart {
    /**
     * Унікальний ідентифікатор сесії ШІ.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ai_sessions_gen")
    @SequenceGenerator(name = "ai_sessions_gen", sequenceName = "ai_sessions_id_seq", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    /**
     * Користувач, який здійснював запит до ШІ (опціонально).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    /**
     * Текст запиту користувача.
     */
    @Column(name = "user_query", columnDefinition = "TEXT", nullable = false)
    private String userQuery;

    /**
     * Відповідь, згенерована ШІ.
     */
    @Column(name = "ai_response", columnDefinition = "TEXT", nullable = false)
    private String aiResponse;

    /**
     * Список ідентифікаторів товарів, рекомендованих ШІ.
     */
    @Column(name = "recommended_item_ids", columnDefinition = "BIGINT[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private Long[] recommendedItemIds;
}
