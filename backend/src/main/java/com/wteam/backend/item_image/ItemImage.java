package com.wteam.backend.item_image;

import com.wteam.backend.common.entity.BaseEntityPart;
import com.wteam.backend.item.Item;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "item_images")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class ItemImage extends BaseEntityPart {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false)
    private Item item;

    @Column(name = "image_url", columnDefinition = "TEXT", nullable = false)
    private String imageUrl;

    @Column(name = "is_main")
    @Builder.Default
    private boolean isMain = false;
}
