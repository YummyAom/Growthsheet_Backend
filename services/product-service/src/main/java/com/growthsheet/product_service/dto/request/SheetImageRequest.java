package com.growthsheet.product_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public record SheetImageRequest(

        @NotBlank(message = "กรุณาระบุ URL รูปภาพ")
        String imageUrl,

        @NotNull(message = "กรุณาระบุลำดับรูป")
        @Min(value = 0, message = "ลำดับรูปต้องเริ่มที่ 0")
        Integer sortOrder
) {}