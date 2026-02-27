package com.growthsheet.admin_service.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.growthsheet.admin_service.dto.SellerApplicationDetailDTO;
import com.growthsheet.admin_service.dto.SellerApplicationSummaryDTO;
import com.growthsheet.admin_service.dto.SellerReviewRequest;
import com.growthsheet.admin_service.dto.SellerReviewResponse;
import com.growthsheet.admin_service.entity.SellerStatus;
import com.growthsheet.admin_service.service.SellerAdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class SellerAdminController {

    private final SellerAdminService sellerAdminService;

    @GetMapping("/")
    public String getHello() {
        return "hello admin seller";
    }

    // /admin/seller-applications?status=PENDING&page=0&size=10
    @GetMapping("/seller-applications")
    public Page<SellerApplicationSummaryDTO> getSellerApplications(
            @RequestParam(defaultValue = "PENDING") String status,
            Pageable pageable) {
        return sellerAdminService.getSellerApplications(status.toUpperCase(), pageable);
    }

    @GetMapping("/seller-applications/{userId}")
    public SellerApplicationDetailDTO getSellerApplicationDetail(
            @PathVariable UUID userId) {

        return sellerAdminService.getSellerDetail(userId);
    }

    // @PutMapping("/seller-applications/{userId}/review")
    // public SellerApplicationDetailDTO reviewSeller(
    // @PathVariable UUID userId,
    // @RequestBody SellerReviewRequest request,
    // @RequestHeader("X-USER-ID") UUID adminId) {

    // return sellerAdminService.reviewSeller(
    // userId,
    // request.getStatus(),
    // request.getComment(),
    // adminId);
    // }

    @PutMapping("/seller-applications/{userId}/approve")
    public SellerReviewResponse approveSeller(
            @PathVariable UUID userId,
            @RequestHeader("X-USER-ID") UUID adminId) {

        return sellerAdminService.reviewSeller(
                userId,
                SellerStatus.APPROVED,
                null,




                
                adminId);
    }

@PutMapping("/seller-applications/{userId}/reject")
public SellerReviewResponse rejectSeller(
        @PathVariable UUID userId,
        @RequestBody SellerReviewRequest request,
        @RequestHeader("X-USER-ID") UUID adminId) {

    return sellerAdminService.reviewSeller(
            userId,
            SellerStatus.REJECTED,
            request.getComment(),
            adminId
    );
}
}