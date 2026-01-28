package com.growthsheet.product_service.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public record CreateSheetRequest(
        @NotBlank(message = "กรุณาระบุชื่อชีทเรียน")
        String title,

        String description,

        @NotNull(message = "กรุณาระบุราคา")
        @DecimalMin(value = "0.0", inclusive = true, message = "ราคาต้องไม่ต่ำกว่า 0")
        BigDecimal price,

        @NotNull(message = "กรุณาระบุหมวดหมู่")
        Long categoryId,

        Long universityId,

        @NotBlank(message = "กรุณาอัปโหลดไฟล์ชีทเรียน")
        String fileUrl,

        @NotBlank(message = "กรุณาระบุรหัสวิชา")
        String courseCode,

        @NotBlank(message = "กรุณาระบุชื่อวิชา")
        String courseName,

        @NotNull(message = "กรุณาระบุชั้นปี")
        @Min(value = 1, message = "ชั้นปีต้องเริ่มที่ 1")
        @Max(value = 8, message = "ชั้นปีระบุได้ไม่เกิน 8")
        Integer studyYear,

        @NotBlank(message = "กรุณาระบุปีการศึกษา")
        @Pattern(regexp = "^[0-9/]+$", message = "ปีการศึกษาควรระบุเป็นตัวเลข เช่น 2568 หรือ 1/2567")
        String academicYear,

        @NotEmpty(message = "กรุณาระบุแฮชแท็กอย่างน้อย 1 รายการ")
        List<String> hashtags,

        @NotBlank(message = "กรุณาอัปโหลดรูปภาพตัวอย่าง")
        String previewUrl
) {
}