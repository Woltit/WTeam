package com.wteam.backend.notification.template;

import com.wteam.backend.common.enums.NotificationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class NotificationMessageGenerator {

    public NotificationMessage generate(final NotificationType type, final Map<String, Object> payload) {
        String itemName = (String) payload.getOrDefault("itemName", "річ");
        String renterName = (String) payload.getOrDefault("renterName", "Користувач");
        String reason = (String) payload.getOrDefault("reason", "не вказано");

        return switch (type) {
            case BOOKING_REQUEST -> new NotificationMessage(
                    "Новий запит на оренду",
                    String.format("%s хоче орендувати «%s».", renterName, itemName)
            );
            case BOOKING_APPROVED -> new NotificationMessage(
                    "Оренду підтверджено",
                    String.format("Власник підтвердив ваше бронювання «%s».", itemName)
            );
            case BOOKING_REJECTED -> new NotificationMessage(
                    "Запит відхилено",
                    String.format("На жаль, власник відхилив запит на «%s».", itemName)
            );
            case BOOKING_CANCELLED -> new NotificationMessage(
                    "Бронювання скасовано",
                    String.format("Бронювання «%s» було скасовано. Причина: %s", itemName, reason)
            );
            case PAYMENT_RECEIVED -> new NotificationMessage(
                    "Оплату успішно отримано",
                    String.format("Кошти за оренду «%s» зараховано.", itemName)
            );
            case REVIEW_LEFT -> new NotificationMessage(
                    "Новий відгук",
                    String.format("Ви отримали новий відгук за оренду «%s».", itemName)
            );
            case VERIFICATION_APPROVED -> new NotificationMessage(
                    "Верифікація успішна",
                    "Ваші документи перевірено. Тепер ви можете повноцінно користуватися платформою."
            );
            case VERIFICATION_REJECTED -> new NotificationMessage(
                    "Помилка верифікації",
                    "Ми не змогли підтвердити ваші дані. Будь ласка, оновіть інформацію в профілі."
            );
            case DISPUTE_OPENED -> new NotificationMessage(
                    "Відкрито суперечку",
                    String.format("Щодо бронювання «%s» відкрито суперечку. Адміністрація скоро зв'яжеться з вами.", itemName)
            );
        };
    }
}
