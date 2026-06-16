package com.wteam.backend.item_image;

import com.wteam.backend.item_image.dto.ItemImageResponse;
import org.springframework.stereotype.Component;

@Component
public class ItemImageMapper {

    public ItemImageResponse toResponse(ItemImage itemImage) {
        if (itemImage == null) return null;
        return new ItemImageResponse(
                itemImage.getId(),
                itemImage.getImageUrl(),
                itemImage.isMain()
        );
    }

}
