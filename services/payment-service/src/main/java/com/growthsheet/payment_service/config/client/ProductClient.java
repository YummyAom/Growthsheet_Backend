package com.growthsheet.payment_service.config.client;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// import com.growthsheet.order_service.dto.response.ProductResponse;

@FeignClient(name = "product-service", url = "${PRODUCT_SERVICE_URL}")
public interface ProductClient {
    @GetMapping("/api/products/{id}") 
    ProductResponse getSheetById(@PathVariable("id") UUID id);
}

record ProductResponse(
    UUID id,
    String title,
    SellerInfo seller, 
    BigDecimal price
) {
    public record SellerInfo(
        UUID id,
        String name
    ) {}
}