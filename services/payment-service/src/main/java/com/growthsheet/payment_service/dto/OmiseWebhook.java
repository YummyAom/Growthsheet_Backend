package com.growthsheet.payment_service.dto;

public record OmiseWebhook(
        String key,
        WebhookData data
) {
    public record WebhookData(
            String id
    ) {}
}
