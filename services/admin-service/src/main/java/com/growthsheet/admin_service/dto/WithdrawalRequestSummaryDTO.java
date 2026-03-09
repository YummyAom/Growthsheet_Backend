package com.growthsheet.admin_service.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.growthsheet.admin_service.entity.WithdrawStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WithdrawalRequestSummaryDTO {

    private UUID id;
    private UUID seller_id;
    private UUID user_id;
    private String sellerPenName;
    private String sellerFullName;
    private BigDecimal amount;
    private WithdrawStatus status;
    private String bank_name;
    private String bank_account_number;
    private String bank_account_name;
    private String note;
    private OffsetDateTime created_at;
}
