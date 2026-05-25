package com.wteam.backend.user_verification_request;

import com.wteam.backend.common.entity.BaseEntity;
import com.wteam.backend.common.enums.DocumentType;
import com.wteam.backend.common.enums.VerificationStatus;
import com.wteam.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "user_verification_requests")
@SuperBuilder
@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
public class UserVerificationRequest extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private DocumentType documentType;

    @Column(name = "document_image_url",nullable = false, columnDefinition = "TEXT")
    private String documentImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by", referencedColumnName = "id")
    private User reviewedBy;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Builder.Default
    private VerificationStatus status = VerificationStatus.PENDING;
}
