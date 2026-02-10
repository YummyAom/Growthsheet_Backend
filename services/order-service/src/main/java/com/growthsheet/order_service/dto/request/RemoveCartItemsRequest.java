package com.growthsheet.order_service.dto.request;

import java.util.List;
import java.util.UUID;

public class RemoveCartItemsRequest {
    private List<UUID> cartItemIds;

    public List<UUID> getCartItemIds() {
        return cartItemIds;
    }

    public void setCartItemIds(List<UUID> cartItemIds) {
        this.cartItemIds = cartItemIds;
    }
}
