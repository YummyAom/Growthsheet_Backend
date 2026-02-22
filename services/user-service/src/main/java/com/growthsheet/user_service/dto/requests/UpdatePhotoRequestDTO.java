package com.growthsheet.user_service.dto.requests;

import jakarta.validation.constraints.NotBlank;

public class UpdatePhotoRequestDTO {

    @NotBlank(message = "photoUrl is required")
    private String photoUrl;

    // --- Getter & Setter Methods ---

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}