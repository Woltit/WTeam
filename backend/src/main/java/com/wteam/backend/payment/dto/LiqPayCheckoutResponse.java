package com.wteam.backend.payment.dto;

public record LiqPayCheckoutResponse(
        String data,
        String signature
) {}
