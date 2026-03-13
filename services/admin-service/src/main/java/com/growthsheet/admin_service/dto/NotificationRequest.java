package com.growthsheet.admin_service.dto;

import java.util.UUID;

public class NotificationRequest {

    private UUID userId;
    private String title;
    private String message;

    public NotificationRequest() {
    }

    public NotificationRequest(UUID userId, String title, String message) {
        this.userId = userId;
        this.title = title;
        this.message = message;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}