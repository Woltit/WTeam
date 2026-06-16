package com.wteam.backend.notification;

import com.wteam.backend.common.enums.NotificationChannel;
import com.wteam.backend.common.enums.NotificationType;
import com.wteam.backend.notification.channel.NotificationSender;
import com.wteam.backend.notification.dto.NotificationEvent;
import com.wteam.backend.notification.template.NotificationMessage;
import com.wteam.backend.notification.template.NotificationMessageGenerator;
import com.wteam.backend.user.User;
import com.wteam.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService Unit Tests")
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private NotificationMessageGenerator messageGenerator;

    @Mock
    private NotificationSender sender;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(
                notificationRepository,
                userRepository,
                notificationMapper,
                messageGenerator,
                List.of(sender)
        );
    }

    @Test
    @DisplayName("process should skip and log warning when user does not exist")
    void process_whenUserDoesNotExist_shouldSkipAndLogWarning() {
        Long userId = 999L;
        NotificationEvent event = new NotificationEvent(
                userId,
                NotificationType.BOOKING_REQUEST,
                NotificationChannel.IN_APP,
                Map.of("itemName", "Item")
        );

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        notificationService.process(event);

        verify(userRepository).findById(userId);
        verifyNoInteractions(notificationRepository, messageGenerator, sender);
    }

    @Test
    @DisplayName("process should create notification when user exists")
    void process_whenUserExists_shouldCreateNotification() {
        Long userId = 1L;
        NotificationEvent event = new NotificationEvent(
                userId,
                NotificationType.BOOKING_REQUEST,
                NotificationChannel.IN_APP,
                Map.of("itemName", "Item")
        );

        User user = new User();
        user.setId(userId);

        NotificationMessage message = new NotificationMessage("Title", "Body");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(messageGenerator.generate(event.notificationType(), event.payload())).thenReturn(message);
        when(sender.supports(event.channel())).thenReturn(true);

        notificationService.process(event);

        verify(userRepository).findById(userId);
        verify(messageGenerator).generate(event.notificationType(), event.payload());
        verify(notificationRepository).save(any(Notification.class));
        verify(sender).send(event);
    }
}
