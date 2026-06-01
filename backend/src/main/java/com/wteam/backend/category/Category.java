package com.wteam.backend.category;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.wteam.backend.common.validation.ValidationConstants.Category.NAME_MAX_LENGTH;
import static com.wteam.backend.common.validation.ValidationConstants.Category.SLUG_MAX_LENGTH;

/**
 * Сутність, що представляє категорію товарів.
 * Категорії можуть бути вкладеними (мати батьківську категорію).
 */
@Entity
@Table(name = "categories")
@SuperBuilder
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Category {
    /**
     * Унікальний ідентифікатор категорії.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categories_gen")
    @SequenceGenerator(name = "categories_gen", sequenceName = "categories_id_seq", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    /**
     * Батьківська категорія (для створення ієрархії).
     * 
     * @see Category
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private Category parent;

    /**
     * Назва категорії.
     */
    @Column(name = "name", length = NAME_MAX_LENGTH, nullable = false)
    private String name;

    /**
     * Унікальний рядок-ідентифікатор для використання в URL.
     */
    @Column(name = "slug", length = SLUG_MAX_LENGTH, unique = true, nullable = false)
    private String slug;

    /**
     * URL іконки категорії.
     */
    @Column(name = "icon_url", columnDefinition = "TEXT")
    private String iconUrl;
}
