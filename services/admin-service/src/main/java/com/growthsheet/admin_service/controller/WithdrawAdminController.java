package com.growthsheet.admin_service.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<String> getWithdrawalRequestById(@PathVariable UUID id) {
        // TODO: implement
        return ResponseEntity.ok("Withdrawal request detail for id: " + id);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<String> approveWithdrawalRequest(@PathVariable UUID id) {
        // TODO: implement
        return ResponseEntity.ok("Withdrawal request approved for id: " + id);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<String> rejectWithdrawalRequest(@PathVariable UUID id) {
        // TODO: implement
        return ResponseEntity.ok("Withdrawal request rejected for id: " + id);
    }
}