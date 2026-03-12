package com.growthsheet.admin_service.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.growthsheet.admin_service.dto.RejectRequest;
import com.growthsheet.admin_service.dto.WithdrawalDetailDTO;
import com.growthsheet.admin_service.dto.WithdrawalApproveResponse;
import com.growthsheet.admin_service.dto.WithdrawalRejectResponse;
import com.growthsheet.admin_service.dto.WithdrawalRequestSummaryDTO;
import com.growthsheet.admin_service.service.WithdrawAdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/withdraw")
@RequiredArgsConstructor
public class WithdrawAdminController {

    private final WithdrawAdminService withdrawAdminService;

    @GetMapping("/list")
    public ResponseEntity<Page<WithdrawalRequestSummaryDTO>> getWithdrawalRequests(
            @RequestParam(defaultValue = "PENDING") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(withdrawAdminService.getWithdrawalRequests(status, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WithdrawalDetailDTO> getWithdrawalRequestById(@PathVariable UUID id) {
        return ResponseEntity.ok(withdrawAdminService.getWithdrawalDetail(id));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<WithdrawalApproveResponse> approveWithdrawalRequest(
            @PathVariable UUID id,
            @RequestParam("slip") MultipartFile slipFile,
            @RequestHeader("X-USER-ID") UUID adminId) {

        WithdrawalApproveResponse response = withdrawAdminService.approveWithdrawal(id, slipFile, adminId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<WithdrawalRejectResponse> rejectWithdrawalRequest(
            @PathVariable UUID id,
            @RequestBody RejectRequest request,
            @RequestHeader("X-USER-ID") UUID adminId) {

        WithdrawalRejectResponse response = withdrawAdminService.rejectWithdrawal(id, request.getComment(), adminId);
        return ResponseEntity.ok(response);
    }
}