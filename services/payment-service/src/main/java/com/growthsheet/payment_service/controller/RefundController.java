package com.growthsheet.payment_service.controller;

import com.growthsheet.payment_service.dto.ApproveRefundDto;
import com.growthsheet.payment_service.dto.CreateRefundRequestDto;
import com.growthsheet.payment_service.dto.RefundResponseDto;
import com.growthsheet.payment_service.dto.RejectRefundDto;
import com.growthsheet.payment_service.service.RefundService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments/refunds")
public class RefundController {

    private final RefundService refundService;

    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    // 🌟 เพิ่ม Endpoint สำหรับดูรายละเอียดและติดตามสถานะ Refund รายตัว (สำหรับ
    // User)
    @GetMapping("/{refundId}")
    public ResponseEntity<RefundResponseDto> getRefundStatus(
            @RequestHeader("X-USER-ID") UUID userId,
            @PathVariable UUID refundId) {
        return ResponseEntity.ok(refundService.getRefundById(refundId, userId));
    }

    @PostMapping
    public ResponseEntity<RefundResponseDto> createRefundRequest(
            @RequestHeader("X-USER-ID") UUID userId,
            @RequestBody CreateRefundRequestDto req) {
        return ResponseEntity.ok(refundService.createRefundRequest(userId, req));
    }

    @GetMapping("/user")
    public ResponseEntity<List<RefundResponseDto>> getUserRefunds(
            @RequestHeader("X-USER-ID") UUID userId) {
        return ResponseEntity.ok(refundService.getRefundsByUser(userId));
    }

    @GetMapping("/admin/pending")
    public ResponseEntity<List<RefundResponseDto>> getPendingRefunds(
            @RequestHeader("X-USER-ID") UUID adminId) {
        // In a real scenario, Gateway should restrict /admin paths to roles
        return ResponseEntity.ok(refundService.getPendingRefunds());
    }

    // ✅ ใหม่ — filter by status
    @GetMapping("/admin")
    public ResponseEntity<List<RefundResponseDto>> getRefundsByStatus(
            @RequestHeader("X-USER-ID") UUID adminId,
            @RequestParam(value = "status", required = false) String status) {
        return ResponseEntity.ok(refundService.getRefundsByStatus(status));
    }

    @PatchMapping("/admin/{refundId}/approve")
    public ResponseEntity<RefundResponseDto> approveRefund(
            @RequestHeader("X-USER-ID") UUID adminId,
            @PathVariable UUID refundId,
            @RequestBody ApproveRefundDto req) {
        return ResponseEntity.ok(refundService.approveRefund(refundId, adminId, req));
    }

    @PatchMapping("/admin/{refundId}/reject")
    public ResponseEntity<RefundResponseDto> rejectRefund(
            @RequestHeader("X-USER-ID") UUID adminId,
            @PathVariable UUID refundId,
            @RequestBody RejectRefundDto req) {
        return ResponseEntity.ok(refundService.rejectRefund(refundId, adminId, req));
    }
}
