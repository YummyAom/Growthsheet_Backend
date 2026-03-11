package com.growthsheet.payment_service.dto;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;
import com.growthsheet.payment_service.entity.RefundStatus;

@Data
public class RefundResponseDto {
    private String sheetName;
    private String sheetFileUrl;
    private UUID id;
    private UUID orderItemId;
    private UUID userId;
    private String reason;
    private String evidenceUrl;
    private String bankAccountName;
    private String bankAccountNumber;
    private String bankName;
    private RefundStatus status;
    private String refundSlipUrl;
    private UUID adminId;
    private String adminComment;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
