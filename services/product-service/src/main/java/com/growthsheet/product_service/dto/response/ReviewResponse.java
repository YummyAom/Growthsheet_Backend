package com.growthsheet.product_service.dto.response;

import com.growthsheet.product_service.dto.UserDTO;
import java.util.UUID;

public record ReviewResponse(
    UUID id,
    UUID sheetId,
    UserDTO user,
    String comment, 
    Integer rating
) {
}