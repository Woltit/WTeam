package com.wteam.backend.item;

import com.wteam.backend.category.Category;
import com.wteam.backend.common.entity.BaseEntityFull;
import com.wteam.backend.common.enums.ItemCondition;
import com.wteam.backend.common.enums.RentingStatus;
import com.wteam.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

import static com.wteam.backend.common.constants.ValidationConstants.Item.*;

@Entity
@Table(name = "items")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class Item extends BaseEntityFull {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    private Category category;

    @Column(name = "title", length = TITLE_MAX_LENGTH, nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "tags", columnDefinition = "TEXT[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private String[] tags;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private ItemCondition condition;

    @Column(name = "price_per_day", precision = PRICE_PRECISION,
            scale = PRICE_SCALE, nullable = false)
    private BigDecimal pricePerDay;

    @Column(name = "price_per_week", precision = PRICE_PRECISION,
            scale = PRICE_SCALE)
    private BigDecimal pricePerWeek;

    @Column(name = "deposit_amount", precision = PRICE_PRECISION,
            scale = PRICE_SCALE, nullable = false)
    private BigDecimal depositAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private RentingStatus status;

    @Column(name = "city", length = 100, nullable = false)
    private String city;

    @Column(name = "address", columnDefinition = "TEXT", nullable = false)
    private String address;

    @Column(name = "latitude", precision = PLACE_PRECISION, scale = PLACE_SCALE)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = PLACE_PRECISION, scale = PLACE_SCALE)
    private BigDecimal longitude;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private boolean isVerified = false;
}
