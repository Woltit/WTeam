package com.wteam.backend.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * The type Base entity part.
 */
@MappedSuperclass
@SuperBuilder
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public abstract class BaseEntityPart {
    /**
     * Дата та час створення запису в базі даних.
     * <p>
     * Заповнюється автоматично за допомогою {@link CreationTimestamp} під час першого збереження (INSERT).
     * Прапорець {@code updatable = false} гарантує, що це значення ніколи не зміниться
     * під час подальших оновлень запису.
     * </p>
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
