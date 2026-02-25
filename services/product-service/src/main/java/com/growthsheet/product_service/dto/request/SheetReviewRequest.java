package com.growthsheet.product_service.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SheetReviewRequest(
        @NotNull(message = "กรุณาให้คะแนน")
        @Min(value = 1, message = "คะแนนต้องไม่ต่ำกว่า 1")
        @Max(value = 5, message = "คะแนนต้องไม่เกิน 5")
        Integer rating,

        @NotBlank(message = "กรุณากรอกความคิดเห็น")
        String comment
) {
}