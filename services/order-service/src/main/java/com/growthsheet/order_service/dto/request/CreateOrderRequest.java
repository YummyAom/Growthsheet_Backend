package com.growthsheet.order_service.dto.request;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest {

    private List<Item> items;

    @Getter
    @Setter
    public static class Item {
        private UUID sheetId;
        private BigDecimal price; 
    }
}
