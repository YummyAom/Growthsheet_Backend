package com.growthsheet.admin_service.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.growthsheet.admin_service.dto.SellerApplicationSummaryDTO;
import com.growthsheet.admin_service.service.SellerAdminService;

import lombok.RequiredArgsConstructor;

@RestController // << เพิ่มตัวนี้
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class SellerAdminController {

    private final SellerAdminService sellerAdminService;

    @GetMapping("/")
    public String getHello() {
        return "hello";
    }

    // /admin/sellers?status=PENDING&page=0&size=10
    @GetMapping("/seller-applications")
    public Page<SellerApplicationSummaryDTO> getSellerApplications(
            @RequestParam(defaultValue = "PENDING") String status,
            Pageable pageable) {
        return sellerAdminService.getSellerApplications(status.toUpperCase(), pageable);
    }

    @GetMapping("/admin/seller-applications/{id}")
    public SellerApplication getById(@PathVariable UUID id) {
        return service.getById(id);
    }
}