package com.growthsheet.admin_service.config.client;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.growthsheet.admin_service.config.FeignOkHttpConfig;
import com.growthsheet.admin_service.dto.sheets.PageResponse;
import com.growthsheet.admin_service.dto.sheets.SheetCardResponse;
import com.growthsheet.admin_service.dto.sheets.SheetDetailResponse;

// import com.growthsheet.order_service.dto.response.ProductResponse;

@FeignClient(name = "product-service", url = "${GATEWAY_SERVICE_URL}", configuration = FeignOkHttpConfig.class)
public interface ProductClient {
        @GetMapping("/products/{sheetId}")
        SheetDetailResponse getSheetById(@PathVariable("sheetId") UUID sheetId);

        @GetMapping("/products")
        PageResponse<SheetCardResponse> getSheets(
                        @RequestParam int page,
                        @RequestParam int size,
                        @RequestParam String sort,
                        @RequestParam(required = false) Boolean isPublished);

        @PatchMapping("/products/{sheetId}/approve")
        void approveSheet(@PathVariable UUID sheetId);

        @PatchMapping("/products/{sheetId}/reject")
        void rejectSheet(@PathVariable UUID sheetId);

}

record ProductResponse(
                UUID id,
                String title,
                SellerInfo seller,
                BigDecimal price) {
        public record SellerInfo(
                        UUID id,
                        String name) {
        }
}