package com.growthsheet.product_service.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public record CreateSheetRequest(
                @NotBlank(message = "กรุณาระบุชื่อชีทเรียน") String title,
                String description,
                @NotNull(message = "กรุณาระบุราคา") BigDecimal price,
                @NotNull(message = "กรุณาระบุหมวดหมู่") Long categoryId,
                Long universityId,
                @NotBlank(message = "กรุณาระบุรหัสวิชา") String courseCode,
                @NotBlank(message = "กรุณาระบุชื่อวิชา") String courseName,
                @NotNull(message = "กรุณาระบุชั้นปี") Integer studyYear,
                @NotBlank(message = "กรุณาระบุปีการศึกษา") String academicYear,
                @NotEmpty(message = "กรุณาระบุแฮชแท็กอย่างน้อย 1 รายการ") List<String> hashtags) {
}
