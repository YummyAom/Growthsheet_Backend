package com.growthsheet.product_service.dto;

import java.util.UUID;

public record UserDTO(
    UUID id,
    String name
) {}
