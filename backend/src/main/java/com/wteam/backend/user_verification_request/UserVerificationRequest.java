package com.wteam.backend.user_verification_request;

import com.wteam.backend.common.entity.BaseEntityFull;
import com.wteam.backend.common.enums.DocumentType;
import com.wteam.backend.common.enums.VerificationStatus;
import com.wteam.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Сутність, що представляє запит користувача на верифікацію профілю.
 * Користувач завантажує документ, який потім перевіряє адміністратор.
 *
 * @see com.wteam.backend.user.User
 * @see com.wteam.backend.common.enums.DocumentType
 * @see com.wteam.backend.common.enums.VerificationStatus
 */
@Entity
@Table(name = "user_verification_requests")
@SuperBuilder
@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
public class UserVerificationRequest extends BaseEntityFull {
    /**
     * Унікальний ідентифікатор запиту на верифікацію.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_verification_requests_gen")
    @SequenceGenerator(name = "user_verification_requests_gen", sequenceName = "user_verification_requests_id_seq", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    
    /**
     * Користувач, який подав запит.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    /**
     * Тип наданого документа (наприклад, паспорт).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private DocumentType documentType;

    /**
     * URL зображення документа.
     */
    @Column(name = "document_image_url", nullable = false, columnDefinition = "TEXT")
    private String documentImageUrl;

    /**
     * Адміністратор, який розглянув запит.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by", referencedColumnName = "id")
    private User reviewedBy;

    /**
     * Причина відмови (якщо запит відхилено).
     */
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    /**
     * Поточний статус розгляду запиту.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Builder.Default
    private VerificationStatus status = VerificationStatus.PENDING;
}
