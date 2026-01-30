package com.growthsheet.order_service.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartRequest {

    private UUID sheetId;
    private String sheetName;
    private String sellerName;
    private BigDecimal price;
}