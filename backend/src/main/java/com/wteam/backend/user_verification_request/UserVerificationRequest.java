package com.wteam.backend.user_verification_request;

import com.wteam.backend.common.entity.BaseEntity;
import com.wteam.backend.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "user_verification_requests")
@SuperBuilder
@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
public class UserVerificationRequest extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
