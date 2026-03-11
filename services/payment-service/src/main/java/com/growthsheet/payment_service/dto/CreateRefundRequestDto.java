package com.growthsheet.payment_service.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class CreateRefundRequestDto {
    private UUID orderItemId;
    private String reason;
    private String evidenceUrl;
    private String bankAccountName;
    private String bankAccountNumber;
    private String bankName;
}
