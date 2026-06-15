package com.wteam.backend.user_device_token;

import com.wteam.backend.common.entity.BaseEntityFull;
import com.wteam.backend.common.enums.DeviceType;
import com.wteam.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "user_device_tokens")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class UserDeviceToken extends BaseEntityFull {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_device_tokens_gen")
    @SequenceGenerator(name = "user_device_tokens_gen", sequenceName = "user_device_tokens_id_seq", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "token", nullable = false, columnDefinition = "TEXT")
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Builder.Default
    private DeviceType type = DeviceType.WEB;
}
