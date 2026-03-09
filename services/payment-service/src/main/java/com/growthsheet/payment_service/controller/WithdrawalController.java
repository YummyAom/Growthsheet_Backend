package com.growthsheet.payment_service.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
     *
     * Response:
     * {
     * "netRevenue": 8500.00, // ยอดขายหลังหัก 15%
     * "withdrawn": 3000.00, // ถอนไปแล้ว + รอโอน
     * "available": 5500.00 // ยอดที่ถอนได้
     * }
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
}
