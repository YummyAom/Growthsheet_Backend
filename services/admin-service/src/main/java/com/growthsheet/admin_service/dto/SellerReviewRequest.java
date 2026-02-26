package com.growthsheet.admin_service.dto;

public class SellerReviewRequest {
    private String status;   // APPROVED / REJECTED
    private String comment;

    // getter setter
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}