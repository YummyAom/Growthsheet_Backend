package com.growthsheet.payment_service.dto;

import lombok.Data;

@Data
public class ApproveRefundDto {
    private String refundSlipUrl;
    private String adminComment;
}
