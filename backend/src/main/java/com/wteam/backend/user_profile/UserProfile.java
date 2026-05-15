package com.wteam.backend.user_profile;

import com.wteam.backend.common.entity.BaseEntity;
import com.wteam.backend.common.enums.Role;
import com.wteam.backend.common.enums.VerificationStatus;
import com.wteam.backend.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "user_profiles")
@SuperBuilder
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "last_name" , length = 100 , nullable = false)
    private String lastName;

    @Column(name = "first_name" , length = 100, nullable = false)
    private String firstName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "phone", length = 15, nullable = false)
    private String phoneNumber;

    @Column(name = "bio")
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private VerificationStatus verificationStatus;
}
