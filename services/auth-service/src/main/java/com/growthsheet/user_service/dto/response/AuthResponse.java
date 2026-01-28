package com.growthsheet.user_service.dto.response;

public record AuthResponse(String userId, String email, String token) {
}
