package com.wteam.backend.payment;

import com.wteam.backend.booking.Booking;
import com.wteam.backend.booking.BookingRepository;
import com.wteam.backend.common.enums.BookingStatus;
import com.wteam.backend.payment.dto.LiqPayCheckoutResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final LiqPayService liqPayService;
    private final LiqPayConfig liqPayConfig;

    @Transactional
    public LiqPayCheckoutResponse createPaymentCheckout(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (booking.getStatus() != BookingStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED bookings can be paid");
        }

        Payment payment = paymentRepository.findByBookingId(bookingId).orElse(new Payment());
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalPrice());
        payment.setStatus(PaymentStatus.PENDING);
        // Generate a unique provider transaction ID or order ID for LiqPay
        String orderId = "WTEAM_" + bookingId + "_" + UUID.randomUUID().toString().substring(0, 8);
        payment.setProviderTransactionId(orderId);
        paymentRepository.save(payment);

        Map<String, Object> params = new HashMap<>();
        params.put("version", 3);
        params.put("action", "pay");
        params.put("amount", payment.getAmount());
        params.put("currency", payment.getCurrency());
        params.put("description", "Оплата оренди " + booking.getItem().getTitle());
        params.put("order_id", orderId);
        params.put("server_url", liqPayConfig.getServerUrl());

        String data = liqPayService.createData(params);
        String signature = liqPayService.createSignature(data);

        return new LiqPayCheckoutResponse(data, signature);
    }

    @Transactional
    public void processLiqPayCallback(String data, String signature) {
        String expectedSignature = liqPayService.createSignature(data);
        if (!expectedSignature.equals(signature)) {
            throw new IllegalArgumentException("Invalid LiqPay signature");
        }

        try {
            String decodedData = new String(java.util.Base64.getDecoder().decode(data), java.nio.charset.StandardCharsets.UTF_8);
            com.fasterxml.jackson.databind.JsonNode jsonNode = new com.fasterxml.jackson.databind.ObjectMapper().readTree(decodedData);
            
            String orderId = jsonNode.get("order_id").asText();
            String status = jsonNode.get("status").asText();

            Payment payment = paymentRepository.findByProviderTransactionId(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Payment not found for orderId: " + orderId));

            if ("success".equals(status) || "wait_compensate".equals(status)) {
                payment.setStatus(PaymentStatus.SUCCESS);
                Booking booking = payment.getBooking();
                booking.setStatus(BookingStatus.PAID);
                bookingRepository.save(booking);
            } else if ("error".equals(status) || "failure".equals(status)) {
                payment.setStatus(PaymentStatus.FAILED);
            } else if ("reversed".equals(status)) {
                payment.setStatus(PaymentStatus.REFUNDED);
                Booking booking = payment.getBooking();
                booking.setStatus(BookingStatus.CANCELLED);
                bookingRepository.save(booking);
            }
            paymentRepository.save(payment);
        } catch (Exception e) {
            throw new RuntimeException("Error processing LiqPay callback", e);
        }
    }
}
