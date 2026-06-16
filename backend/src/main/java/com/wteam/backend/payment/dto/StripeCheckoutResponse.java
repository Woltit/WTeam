package com.wteam.backend.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StripeCheckoutResponse {
    private String url;
}
