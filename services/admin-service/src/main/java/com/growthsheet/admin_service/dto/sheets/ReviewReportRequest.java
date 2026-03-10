package com.growthsheet.admin_service.dto.sheets;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO สำหรับ admin ส่ง review report (เปลี่ยน status + เพิ่มหมายเหตุ)
 */
@Getter
@Setter
public class ReviewReportRequest {
    private String status;    // REVIEWED หรือ DISMISSED
    private String adminNote; // หมายเหตุจาก admin (optional)
    private Boolean suspendSheet; // ถ้าระบุเป็น true จะทำการปรับชีทให้ไม่เป็น public
}
