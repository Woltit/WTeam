package com.wteam.backend.payment;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.wteam.backend.booking.Booking;
import com.wteam.backend.booking.BookingRepository;
import com.wteam.backend.common.enums.BookingStatus;
import com.wteam.backend.payment.dto.StripeCheckoutResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final StripeConfig stripeConfig;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Transactional
    public StripeCheckoutResponse createPaymentCheckout(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (booking.getStatus() != BookingStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED bookings can be paid");
        }

        Payment payment = paymentRepository.findByBookingId(bookingId).orElse(new Payment());
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalPrice());

        if (payment.getProviderTransactionId() == null || payment.getStatus() == PaymentStatus.FAILED || payment.getStatus() == PaymentStatus.REFUNDED) {
            String orderId = "WTEAM_" + bookingId + "_" + UUID.randomUUID().toString().substring(0, 8);
            payment.setProviderTransactionId(orderId);
        }

        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);

        try {
            // Convert to smallest currency unit (e.g. cents for USD, kopecks for UAH)
            long amountInSmallestUnit = payment.getAmount().multiply(new BigDecimal("100")).longValue();

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(frontendUrl + "/my-bookings?payment=success&bookingId=" + bookingId)
                    .setCancelUrl(frontendUrl + "/my-bookings?payment=cancel")
                    .setClientReferenceId(payment.getProviderTransactionId())
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency(payment.getCurrency().toLowerCase())
                                                    .setUnitAmount(amountInSmallestUnit)
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Оренда: " + booking.getItem().getTitle())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);
            
            // Optionally store the Stripe Session ID if needed
            payment.setProviderTransactionId(session.getId());
            paymentRepository.save(payment);

            return new StripeCheckoutResponse(session.getUrl());
        } catch (StripeException e) {
            throw new RuntimeException("Failed to create Stripe checkout session", e);
        }
    }

    @Transactional
    public void processStripeWebhook(String payload, String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, stripeConfig.getWebhookSecret());

            if ("checkout.session.completed".equals(event.getType())) {
                EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
                if (dataObjectDeserializer.getObject().isPresent()) {
                    StripeObject stripeObject = dataObjectDeserializer.getObject().get();
                    if (stripeObject instanceof Session session) {
                        handleSuccessfulPayment(session.getId());
                    }
                }
            }
        } catch (SignatureVerificationException e) {
            throw new IllegalArgumentException("Invalid Stripe signature");
        } catch (Exception e) {
            throw new RuntimeException("Error processing Stripe webhook", e);
        }
    }

    private void handleSuccessfulPayment(String sessionId) {
        Optional<Payment> paymentOpt = paymentRepository.findByProviderTransactionId(sessionId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setStatus(PaymentStatus.SUCCESS);
            Booking booking = payment.getBooking();
            booking.setStatus(BookingStatus.PAID);
            bookingRepository.save(booking);
            paymentRepository.save(payment);
        }
    }

    @Transactional
    public void verifyPaymentStatus(Long bookingId) {
        // With Stripe Checkout and Webhooks, manual verification is typically done by fetching the Session.
        // For local development, if webhooks fail, the frontend might call this on success_url return.
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return;
        }

        try {
            Session session = Session.retrieve(payment.getProviderTransactionId());
            if ("complete".equals(session.getStatus()) && "paid".equals(session.getPaymentStatus())) {
                payment.setStatus(PaymentStatus.SUCCESS);
                Booking booking = payment.getBooking();
                booking.setStatus(BookingStatus.PAID);
                bookingRepository.save(booking);
                paymentRepository.save(payment);
            }
        } catch (StripeException e) {
            throw new RuntimeException("Failed to retrieve Stripe session", e);
        }
    }
}
