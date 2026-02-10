package com.growthsheet.order_service.dto.request;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AddToCartRequest {
    private UUID sheetId;

    public UUID getSheetId() {
        return sheetId;
    }

    public void setSheetId(UUID sheetId) {
        this.sheetId = sheetId;
    }
}