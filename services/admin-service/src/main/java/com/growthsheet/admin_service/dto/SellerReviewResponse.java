package com.growthsheet.admin_service.dto;
import com.growthsheet.admin_service.entity.SellerStatus;
public class SellerReviewResponse {

    private String message;
    private String fullName;
    private String penName;
    private SellerStatus status;
    private String adminComment;

    public SellerReviewResponse(String message,
            String fullName,
            String penName,
            SellerStatus status,
            String adminComment) {
        this.message = message;
        this.fullName = fullName;
        this.penName = penName;
        this.status = status;
        this.adminComment = adminComment;
    }

    // getters
    public String getMessage() {
        return message;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPenName() {
        return penName;
    }

    public SellerStatus getStatus() {
        return status;
    }

    public String getAdminComment() {
        return adminComment;
    }
}