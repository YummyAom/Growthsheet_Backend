package com.growthsheet.admin_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.growthsheet.admin_service.entity.SellerStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerApplicationSummaryDTO {

    private UUID user_id;
    private String pen_name;
    private String full_name;
    private String university;

    private SellerStatus is_verified;   // PENDING / APPROVED / REJECTED

    private LocalDateTime created_at;
    private LocalDateTime reviewed_at;
}