package com.wteam.backend.item_image;

import com.wteam.backend.common.entity.BaseEntityPart;
import com.wteam.backend.item.Item;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Сутність, що представляє зображення товару.
 * Одне зображення може бути помічене як головне (is_main).
 *
 * @see com.wteam.backend.item.Item
 */
@Entity
@Table(name = "item_images")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class ItemImage extends BaseEntityPart {
    /**
     * Унікальний ідентифікатор зображення.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_images_gen")
    @SequenceGenerator(name = "item_images_gen", sequenceName = "item_images_id_seq", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    /**
     * Товар, до якого відноситься зображення.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false)
    private Item item;

    /**
     * URL зображення.
     */
    @Column(name = "image_url", columnDefinition = "TEXT", nullable = false)
    private String imageUrl;

    /**
     * Прапорець, що вказує, чи є це зображення основним для товару.
     */
    @Column(name = "is_main")
    @Builder.Default
    private boolean isMain = false;
}
