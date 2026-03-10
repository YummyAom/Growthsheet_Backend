package com.growthsheet.admin_service.dto.sheets;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO สำหรับรับข้อมูล report จาก product-service ผ่าน Feign
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SheetReportResponse {
    private UUID id;
    private UUID sheetId;
    private UUID reporterId;
    private String reason;
    private String status;
    private UUID adminId;
    private String adminNote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String fileUrl;
}
