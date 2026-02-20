package com.growthsheet.user_service.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePhotoRequestDTO {

    @NotBlank(message = "photoUrl is required")
    private String photoUrl;
}