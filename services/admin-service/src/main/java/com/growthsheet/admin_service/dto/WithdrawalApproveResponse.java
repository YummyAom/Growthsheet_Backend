package com.growthsheet.admin_service.dto;

import java.util.UUID;

import com.growthsheet.admin_service.entity.WithdrawStatus;

public record WithdrawalApproveResponse(
        String message,
        UUID withdrawalId,
        String slipUrl,
        WithdrawStatus status) {
}
