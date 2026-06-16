package com.wteam.backend.item_image;

import com.wteam.backend.cloudinary.ImageService;
import com.wteam.backend.exception.item.ItemNotFoundException;
import com.wteam.backend.exception.item.ItemImageNotFoundException;
import com.wteam.backend.exception.item.ItemImageAccessDeniedException;
import com.wteam.backend.exception.cloudinary.ImageUploadException;
import com.wteam.backend.item.Item;
import com.wteam.backend.item.ItemRepository;
import com.wteam.backend.item_image.dto.ItemImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Сервіс для керування зображеннями товарів.
 * <p>
 * Обробляє бізнес-логіку, пов'язану із завантаженням, видаленням та отриманням зображень товарів.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ItemImageService {
    private final ItemImageRepository itemImageRepository;
    private final ItemRepository itemRepository;
    private final ImageService imageService;
    private final ItemImageMapper itemImageMapper;

    @Transactional
    public ItemImageResponse uploadItemImage(Long itemId, Long userId, MultipartFile file, boolean isMain) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        if (!item.getOwner().getId().equals(userId)) {
            throw new ItemImageAccessDeniedException("You are not the owner of this item");
        }

        if (isMain) {
            item.getImages().forEach(img -> img.setMain(false));
            for (ItemImage img : item.getImages()) {
                img.setMain(false);
                itemImageRepository.save(img);
            }
        }

        try {
            String imageUrl = imageService.uploadImage(file);
            ItemImage itemImage = ItemImage.builder()
                    .item(item)
                    .imageUrl(imageUrl)
                    .isMain(isMain)
                    .build();

            return itemImageMapper.toResponse(itemImageRepository.save(itemImage));
        } catch (IOException e) {
            throw new ImageUploadException("Failed to upload item image", e);
        }
    }

    @Transactional
    public void deleteItemImage(Long imageId, Long userId) {
        ItemImage itemImage = itemImageRepository.findById(imageId)
                .orElseThrow(() -> new ItemImageNotFoundException(imageId));

        if (!itemImage.getItem().getOwner().getId().equals(userId)) {
            throw new ItemImageAccessDeniedException("You are not the owner of this item");
        }

        itemImageRepository.delete(itemImage);
    }
}
