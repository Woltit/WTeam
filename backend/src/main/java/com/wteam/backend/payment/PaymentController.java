package com.wteam.backend.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Оплата (LiqPay)", description = "API для роботи з платежами через платіжну систему LiqPay")
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Створити чекаут", description = "Генерує data та signature для віджета LiqPay для конкретного бронювання.")
    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<com.wteam.backend.payment.dto.LiqPayCheckoutResponse> createPaymentCheckout(@RequestParam Long bookingId) {
        return ResponseEntity.ok(paymentService.createPaymentCheckout(bookingId));
    }

    @Operation(summary = "LiqPay Callback", description = "Службовий ендпоінт (вебхук), куди LiqPay надсилає статус платежу.")
    @PostMapping(value = "/callback", consumes = org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> liqPayCallback(@RequestParam String data, @RequestParam String signature) {
        paymentService.processLiqPayCallback(data, signature);
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
