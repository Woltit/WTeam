package com.wteam.backend.notification.channel.email;

import com.wteam.backend.common.enums.NotificationChannel;
import com.wteam.backend.common.enums.NotificationType;
import com.wteam.backend.notification.dto.NotificationEvent;
import com.wteam.backend.user.User;
import com.wteam.backend.user.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
class ResendEmailIntegrationTest {

    @Autowired
    private EmailSenderService emailSenderService;

    // Підміняємо звернення до БД, щоб відправити лист на вашу реальну пошту
    @MockitoBean
    private UserRepository userRepository;

    @Test
    @Disabled("Запускати лише вручну через IDE! Анотація запобігає витраті лімітів Resend під час збірки.")
    void testSendRealEmailViaResend() {
        // УВАГА: Для тестування Resend без підтвердженого домену,
        // лист дійде ТІЛЬКИ на ту пошту, на яку зареєстровано акаунт Resend.
        String yourRealEmail = "lisovskyiarsenii@gmail.com";
        Long testUserId = 999L;

        // 1. Вчимо мок-репозиторій повертати користувача з вашою поштою
        User mockUser = new User();
        mockUser.setId(testUserId);
        mockUser.setEmail(yourRealEmail);
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(mockUser));

        // 2. Формуємо тестові дані (Payload)
        Map<String, Object> payload = Map.of(
                "itemName", "Тестовий інструмент (Resend Check)",
                "renterName", "Джон Доу"
        );

        // Переконайтеся, що BOOKING_CREATED існує у вашому enum NotificationType
        NotificationEvent event = new NotificationEvent(
                testUserId,
                NotificationType.BOOKING_REQUEST,
                NotificationChannel.EMAIL,
                payload
        );

        // 3. Виконуємо реальну відправку
        emailSenderService.send(event);

        System.out.println("✅ Запит на відправку виконано! Перевіряйте поштову скриньку: " + yourRealEmail);
    }
}
