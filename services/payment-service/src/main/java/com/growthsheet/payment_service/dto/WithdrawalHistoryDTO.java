package com.growthsheet.payment_service.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO สำหรับแสดงรายการประวัติการถอนเงินของ seller
 */
@Getter
@Setter
public class WithdrawalHistoryDTO {

    private UUID id;
    private BigDecimal amount;
    private String status;
    private String bankName;
    private String bankAccountNumber;
    private String bankAccountName;
    private String note;
    private String adminComment;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
