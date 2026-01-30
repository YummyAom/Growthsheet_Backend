package com.growthsheet.order_service.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartResponse {

    private UUID cartId;
    private UUID userId;
    private BigDecimal totalPrice;
    private List<Item> items;

    @Getter
    @Setter
    public static class Item {
        private UUID id;
        private UUID sheetId;
        private String sheetName;
        private String sellerName;
        private BigDecimal price;
    }
}