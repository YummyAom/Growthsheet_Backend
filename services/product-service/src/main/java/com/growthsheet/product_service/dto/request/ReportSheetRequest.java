package com.growthsheet.product_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReportSheetRequest {

    @NotBlank(message = "กรุณาระบุเหตุผลในการรายงาน")
    private String reason;
}
