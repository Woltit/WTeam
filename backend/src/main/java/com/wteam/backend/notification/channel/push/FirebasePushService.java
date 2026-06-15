package com.wteam.backend.notification.channel.push;

import com.google.firebase.messaging.*;
import com.wteam.backend.common.enums.NotificationChannel;
import com.wteam.backend.exception.notification.NotificationDeliveryException;
import com.wteam.backend.notification.channel.NotificationSender;
import com.wteam.backend.notification.dto.NotificationEvent;
import com.wteam.backend.notification.template.NotificationMessage;
import com.wteam.backend.notification.template.NotificationMessageGenerator;
import com.wteam.backend.user_device_token.UserDeviceTokenService;
import com.wteam.backend.user_device_token.dto.UserDeviceTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FirebasePushService implements NotificationSender {
    private final NotificationMessageGenerator messageGenerator;
    private final UserDeviceTokenService tokenService;

    @Override
    public void send(final NotificationEvent event) {
        Long userId = event.recipientUserId();

        List<String> deviceTokens = tokenService.getTokensByUserId(userId)
                .stream()
                .map(UserDeviceTokenResponse::token)
                .toList();

        if (deviceTokens.isEmpty()) {
            log.debug("Користувач {} не має активних FCM токенів. Відправка скасована.", userId);
            return;
        }

        NotificationMessage messageText = messageGenerator.generate(event.notificationType(), event.payload());

        Notification notification = Notification.builder()
                .setTitle(messageText.title())
                .setBody(messageText.body())
                .build();

        MulticastMessage multicastMessage = MulticastMessage.builder()
                .addAllTokens(deviceTokens)
                .setNotification(notification)
                .putAllData(convertPayloadToStringMap(event.payload()))
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(multicastMessage);

            if (response.getFailureCount() > 0) {
                handleFailedTokens(response.getResponses(), deviceTokens);
            }
            log.info("Відправлено пуш-сповіщення користувачу {}. Успішно: {}", userId, response.getSuccessCount());
        } catch (FirebaseMessagingException e) {
            throw new NotificationDeliveryException("Failed to send push notification to user " + userId, e);
        }
    }

    @Override
    public boolean supports(final NotificationChannel channel) {
        return channel == NotificationChannel.PUSH;
    }

    private Map<String, String> convertPayloadToStringMap(final Map<String, Object> payload) {
        return payload.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue() != null ? String.valueOf(e.getValue()) : ""
                ));
    }

    private void handleFailedTokens(
            final List<SendResponse> responses,
            final List<String> deviceTokens
    ) {
        for (int i = 0; i < responses.size(); i++) {
            SendResponse sendResponse = responses.get(i);

            if (!sendResponse.isSuccessful()) {
                MessagingErrorCode errorCode = sendResponse.getException().getMessagingErrorCode();
                if (errorCode == MessagingErrorCode.UNREGISTERED || errorCode == MessagingErrorCode.INVALID_ARGUMENT) {
                    String failedToken = deviceTokens.get(i);
                    log.warn("Видалення недійсного FCM токена: {}", failedToken);
                    tokenService.deleteToken(failedToken);
                }
            }
        }
    }
}
