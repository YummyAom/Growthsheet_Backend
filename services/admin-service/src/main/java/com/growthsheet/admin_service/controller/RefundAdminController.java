package com.growthsheet.admin_service.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.growthsheet.admin_service.dto.RejectRequest;
import com.growthsheet.admin_service.service.RefundAdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/refunds")
@RequiredArgsConstructor
public class RefundAdminController {

    private final RefundAdminService refundAdminService;

    // ถ้าไม่ส่ง status จะดึงทั้งหมด
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getRefunds(
            @RequestParam(value = "status", required = false) String status,
            @RequestHeader("X-USER-ID") UUID adminId) {
        return ResponseEntity.ok(refundAdminService.getRefundsByStatus(status, adminId));
    }

    @PatchMapping("/{refundId}/approve")
    public ResponseEntity<Map<String, Object>> approveRefund(
            @PathVariable UUID refundId,
            @RequestParam("slip") MultipartFile slipFile,
            @RequestParam(value = "comment", required = false) String adminComment,
            @RequestHeader("X-USER-ID") UUID adminId) {

        return ResponseEntity.ok(refundAdminService.approveRefund(refundId, slipFile, adminComment, adminId));
    }

    @PatchMapping("/{refundId}/reject")
    public ResponseEntity<Map<String, Object>> rejectRefund(
            @PathVariable UUID refundId,
            @RequestBody RejectRequest request,
            @RequestHeader("X-USER-ID") UUID adminId) {

        return ResponseEntity.ok(refundAdminService.rejectRefund(refundId, request.getComment(), adminId));
    }
}
