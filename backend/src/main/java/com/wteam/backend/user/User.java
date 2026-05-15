package com.wteam.backend.user;


import com.wteam.backend.common.entity.BaseEntity;
import com.wteam.backend.common.enums.Role;
import com.wteam.backend.user_profile.UserProfile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users")
@SuperBuilder
@NoArgsConstructor @AllArgsConstructor
@Setter @Getter
public class User extends BaseEntity {
    @Column(name = "email", length = 255, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private UserProfile userProfile;
}
