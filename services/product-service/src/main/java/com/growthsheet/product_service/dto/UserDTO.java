package com.growthsheet.product_service.dto;

import java.util.UUID;

public record UserDTO(
    UUID userId,
    String name,
    String userPhotoUrl
) {}
