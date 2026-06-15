package com.wteam.backend.notification.channel.email;

import com.wteam.backend.common.enums.NotificationChannel;
import com.wteam.backend.exception.notification.NotificationDeliveryException;
import com.wteam.backend.exception.user.UserNotFoundException;
import com.wteam.backend.notification.channel.NotificationSender;
import com.wteam.backend.notification.dto.NotificationEvent;
import com.wteam.backend.notification.template.NotificationMessage;
import com.wteam.backend.notification.template.NotificationMessageGenerator;
import com.wteam.backend.user.User;
import com.wteam.backend.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSenderService implements NotificationSender {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final UserRepository userRepository;
    private final NotificationMessageGenerator messageGenerator;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void send(NotificationEvent event) {
        Long userId = event.recipientUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getEmail() == null) {
            throw new NotificationDeliveryException("User does not have an email address");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());

            NotificationMessage messageText = messageGenerator.generate(event.notificationType(), event.payload());

            helper.setSubject("RentGo | " + messageText.title());

            Context context = new Context();
            context.setVariable("title", messageText.title());
            context.setVariable("body", messageText.body());
            context.setVariables(event.payload());

            String htmlContent = templateEngine.process("notification-template", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email успішно відправлено користувачу {}", userId);
        } catch (MessagingException e) {
            throw new NotificationDeliveryException("Failed to send email to user " + userId, e);
        }
    }

    @Override
    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.EMAIL;
    }
}
