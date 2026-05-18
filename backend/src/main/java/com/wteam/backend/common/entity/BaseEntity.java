package com.wteam.backend.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * Абстрактний базовий клас для всіх JPA-сутностей у системі.
 * <p>
 * Використовує анотацію {@link MappedSuperclass}, завдяки чому його поля
 * (ідентифікатор та стовпці аудиту) автоматично успадковуються дочірніми сутностями
 * й додаються до їхніх таблиць. Окрема таблиця для цього класу в БД не створюється.
 * </p>
 */
@MappedSuperclass
@SuperBuilder
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public abstract class BaseEntity {

    /**
     * Унікальний первинний ключ сутності.
     * <p>
     * Налаштований на стратегію {@link GenerationType#SEQUENCE}, яка забезпечує
     * найкращу продуктивність та пакетне вставлення записів (batching) у PostgreSQL.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

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

    /**
     * Дата та час останньої модифікації запису в базі даних.
     * <p>
     * Автоматично перезаписується фреймворком за допомогою {@link UpdateTimestamp}
     * під час кожної операції оновлення (UPDATE).
     * </p>
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
