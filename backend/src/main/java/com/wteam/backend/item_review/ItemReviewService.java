package com.wteam.backend.item_review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Сервіс для керування відгуками на товари.
 * <p>
 * Обробляє бізнес-логіку, пов'язану зі створенням, редагуванням та отриманням відгуків на товари.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ItemReviewService {
    private final ItemReviewRepository itemReviewRepository;
}
