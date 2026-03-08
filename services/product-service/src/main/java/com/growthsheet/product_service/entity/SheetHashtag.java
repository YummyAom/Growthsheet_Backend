package com.growthsheet.product_service.entity;

import java.time.Instant;

import jakarta.persistence.*;

@Entity
@Table(name = "sheet_hashtags")
public class SheetHashtag {

    @EmbeddedId
    private SheetHashtagId id;

    @ManyToOne
    @MapsId("sheetId")
    @JoinColumn(name = "sheet_id")
    private Sheet sheet;

    @ManyToOne
    @MapsId("hashtagId")
    @JoinColumn(name = "hashtag_id")
    private Hashtag hashtag;

    @Column(name = "source")
    private String source;

    private Double confidence;

    private Boolean approved;

    @Column(name = "created_at")
    private Instant createdAt;

    public SheetHashtagId getId() {
        return id;
    }

    public void setId(SheetHashtagId id) {
        this.id = id;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    public Hashtag getHashtag() {
        return hashtag;
    }

    public void setHashtag(Hashtag hashtag) {
        this.hashtag = hashtag;
    }

    public String getSource() {
        return source;
    }

    public Double getConfidence() {
        return confidence;
    }

    public Boolean getApproved() {
        return approved;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}