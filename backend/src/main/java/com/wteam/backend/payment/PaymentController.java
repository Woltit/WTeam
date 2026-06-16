package com.wteam.backend.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<com.wteam.backend.payment.dto.LiqPayCheckoutResponse> createPaymentCheckout(@RequestParam Long bookingId) {
        return ResponseEntity.ok(paymentService.createPaymentCheckout(bookingId));
    }

    @PostMapping(value = "/callback", consumes = org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> liqPayCallback(@RequestParam String data, @RequestParam String signature) {
        paymentService.processLiqPayCallback(data, signature);
        return ResponseEntity.ok().build();
    }
}
