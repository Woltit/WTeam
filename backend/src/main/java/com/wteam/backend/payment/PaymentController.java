package com.wteam.backend.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Оплата (Stripe)", description = "API для роботи з платежами через платіжну систему Stripe")
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Створити чекаут", description = "Генерує Stripe Checkout Session URL для конкретного бронювання.")
    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<com.wteam.backend.payment.dto.StripeCheckoutResponse> createPaymentCheckout(@RequestParam Long bookingId) {
        return ResponseEntity.ok(paymentService.createPaymentCheckout(bookingId));
    }

    @Operation(summary = "Stripe Webhook", description = "Службовий ендпоінт, куди Stripe надсилає статус платежу.")
    @PostMapping(value = "/webhook")
    public ResponseEntity<String> stripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        paymentService.processStripeWebhook(payload, sigHeader);
        return ResponseEntity.ok("OK");
    }

    @Operation(summary = "Перевірити статус", description = "Додаткова перевірка статусу платежу в разі, якщо вебхук не дійшов (наприклад, при локальній розробці).")
    @PostMapping("/verify")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> verifyPaymentStatus(@RequestParam Long bookingId) {
        paymentService.verifyPaymentStatus(bookingId);
        return ResponseEntity.ok().build();
    }
}

