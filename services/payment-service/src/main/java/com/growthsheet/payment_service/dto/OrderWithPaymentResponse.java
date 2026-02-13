package com.growthsheet.payment_service.dto;

import com.growthsheet.payment_service.entity.Payment;

public record OrderWithPaymentResponse(
        OrderResponse order,
        Payment payment
) {}