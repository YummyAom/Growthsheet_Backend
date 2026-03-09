package com.growthsheet.product_service.entity;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class SheetHashtagId implements Serializable {

    @Column(name = "sheet_id")
    private UUID sheetId;

    @Column(name = "hashtag_id")
    private Long hashtagId;

    public UUID getSheetId() {
        return sheetId;
    }

    public Long getHashtagId() {
        return hashtagId;
    }

    public void setSheetId(UUID sheetId) {
        this.sheetId = sheetId;
    }

    public void setHashtagId(Long hashtagId) {
        this.hashtagId = hashtagId;
    }
}