package com.growthsheet.payment_service.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.growthsheet.payment_service.dto.CreateWithdrawalRequest;
import com.growthsheet.payment_service.dto.SellerBalanceResponse;
import com.growthsheet.payment_service.dto.WithdrawalHistoryDTO;
import com.growthsheet.payment_service.service.WithdrawalService;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments/withdrawals")
@RequiredArgsConstructor
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    /**
     * ดูประวัติการถอนเงินของ seller (ที่ขอถอนไป รอ admin อนุมัติ)
     * GET /api/payments/withdrawals/history?page=0&size=10
     */
    @GetMapping("/history")
    public ResponseEntity<Page<WithdrawalHistoryDTO>> getWithdrawalHistory(
            @RequestHeader("X-USER-ID") UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<WithdrawalHistoryDTO> history = withdrawalService.getWithdrawalHistory(userId, pageable);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * ดูยอดเงินที่ถอนได้ของ seller
     * GET /api/payments/withdrawals/balance
     */
    @GetMapping("/balance")
    public ResponseEntity<?> getSellerBalance(
            @RequestHeader("X-USER-ID") UUID userId) {

        try {
            SellerBalanceResponse balance = withdrawalService.getSellerBalance(userId);
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * สร้างคำขอถอนเงินใหม่
     * POST /api/payments/withdrawals/request
     */
    @PostMapping("/request")
    public ResponseEntity<?> createWithdrawalRequest(
            @RequestHeader("X-USER-ID") UUID userId,
            @RequestBody CreateWithdrawalRequest req) {

        try {
            WithdrawalHistoryDTO result = withdrawalService.createWithdrawalRequest(userId, req);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "สร้างคำขอถอนเงินเรียบร้อย รอ admin อนุมัติ",
                    "data", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}

