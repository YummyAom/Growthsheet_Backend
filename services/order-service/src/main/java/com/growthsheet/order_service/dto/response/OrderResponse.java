package com.growthsheet.order_service.dto.response;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponse {

    private UUID orderId;
    private UUID userId;
    private String status;
    private BigDecimal totalPrice;

    private List<Item> items;

    @Getter
    @Setter
    public static class Item {
        private UUID sheetId;
        private BigDecimal price;
    }
}
