package com.wteam.backend.item;

import com.wteam.backend.category.Category;
import com.wteam.backend.common.entity.BaseEntityFull;
import com.wteam.backend.common.enums.ItemCondition;
import com.wteam.backend.common.enums.RentingStatus;
import com.wteam.backend.item_image.ItemImage;
import com.wteam.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.wteam.backend.common.validation.ValidationConstants.Item.TITLE_MAX_LENGTH;

/**
 * Сутність, що представляє товар або послугу для оренди.
 * Містить детальну інформацію про предмет, його стан, ціну та місцезнаходження.
 *
 * @see User
 * @see Category
 * @see com.wteam.backend.common.enums.ItemCondition
 * @see com.wteam.backend.common.enums.RentingStatus
 */
@Entity
@Table(name = "items")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class Item extends BaseEntityFull {
    /**
     * Унікальний ідентифікатор товару.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "items_gen")
    @SequenceGenerator(name = "items_gen", sequenceName = "items_id_seq", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    /**
     * Власник товару.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private User owner;

    /**
     * Категорія, до якої належить товар.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    private Category category;

    /**
     * Заголовок оголошення.
     */
    @Column(name = "title", length = TITLE_MAX_LENGTH, nullable = false)
    private String title;

    /**
     * Детальний опис товару.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Масив тегів для пошуку.
     */
    @Column(name = "tags", columnDefinition = "TEXT[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private String[] tags;

    /**
     * Стан товару.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "condition", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private ItemCondition condition;

    /**
     * Ціна оренди за один день.
     */
    @Column(name = "price_per_day", precision = 10, scale = 2, nullable = false)
    private BigDecimal pricePerDay;

    /**
     * Ціна оренди за один тиждень (опціонально).
     */
    @Column(name = "price_per_week", precision = 10, scale = 2)
    private BigDecimal pricePerWeek;

    /**
     * Сума застави.
     */
    @Column(name = "deposit_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal depositAmount;

    /**
     * Поточний статус доступності товару.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private RentingStatus status;

    /**
     * Місто, де знаходиться товар.
     */
    @Column(name = "city", length = 100, nullable = false)
    private String city;

    /**
     * Точна адреса місцезнаходження.
     */
    @Column(name = "address", columnDefinition = "TEXT", nullable = false)
    private String address;

    /**
     * Широта для відображення на мапі.
     */
    @Column(name = "latitude", precision = 9, scale = 6)
    private BigDecimal latitude;

    /**
     * Довгота для відображення на мапі.
     */
    @Column(name = "longitude", precision = 9, scale = 6)
    private BigDecimal longitude;

    /**
     * Прапорець, що вказує, чи був товар верифікований модератором.
     */
    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private boolean isVerified = false;

    /**
     * Рейтинг товару (на основі відгуків).
     */
    @Column(name = "rating", precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal rating = BigDecimal.ZERO;

    /**
     * Загальна кількість відгуків на товар.
     */
    @Column(name = "total_reviews")
    @Builder.Default
    private Integer totalReviews = 0;

    /**
     * Зображення товару.
     */
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemImage> images = new ArrayList<>();
}
