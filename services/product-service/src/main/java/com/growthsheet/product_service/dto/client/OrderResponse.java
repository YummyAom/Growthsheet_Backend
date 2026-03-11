package com.growthsheet.product_service.dto.client;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;

public class OrderResponse {

    private UUID orderId;
    private UUID userId;
    private String status;
    private BigDecimal totalPrice;
    private List<Item> items;

    // Default Constructor (จำเป็นสำหรับ JSON Deserialization)
    public OrderResponse() {
    }

    // --- Getter & Setter Methods ---

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    // --- Static Inner Class ---
    public static class Item {
        private UUID sheetId;
        private String sheetName;
        private String sellerName;
        private BigDecimal price;
        private Boolean isRefunded;

        public Item() {
        }

        public UUID getSheetId() {
            return sheetId;
        }

        public void setSheetId(UUID sheetId) {
            this.sheetId = sheetId;
        }

        public String getSheetName() {
            return sheetName;
        }

        public void setSheetName(String sheetName) {
            this.sheetName = sheetName;
        }

        public String getSellerName() {
            return sellerName;
        }

        public void setSellerName(String sellerName) {
            this.sellerName = sellerName;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public Boolean getIsRefunded() {
            return isRefunded;
        }

        public void setIsRefunded(Boolean isRefunded) {
            this.isRefunded = isRefunded;
        }
    }
}