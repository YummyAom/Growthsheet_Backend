package com.growthsheet.admin_service.dto;

import java.util.UUID;

import com.growthsheet.admin_service.entity.WithdrawStatus;

public record WithdrawalRejectResponse(
        String message,
        UUID withdrawalId,
        String adminComment,
        WithdrawStatus status) {
}
