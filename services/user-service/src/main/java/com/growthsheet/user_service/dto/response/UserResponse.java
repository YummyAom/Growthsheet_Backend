package com.growthsheet.user_service.dto.response;

public record UserResponse(
        String id,
        String email,
        String role
) {}