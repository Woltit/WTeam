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
    public ResponseEntity<Map<String, String>> createPaymentUrl(@RequestParam Long bookingId) {
        String url = paymentService.createPaymentUrl(bookingId);
        return ResponseEntity.ok(Map.of("payUrl", url));
    }

    @PostMapping("/callback-stub")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> callbackStub(@RequestParam Long paymentId, @RequestParam boolean success) {
        paymentService.processCallbackStub(paymentId, success);
        return ResponseEntity.ok().build();
    }
}
