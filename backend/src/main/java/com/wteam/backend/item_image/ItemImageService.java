package com.wteam.backend.item_image;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
