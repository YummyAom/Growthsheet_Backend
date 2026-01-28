package com.growthsheet.user_service.dto.response;

import com.growthsheet.user_service.respository.UserRepository;

public record LoginResponse(
    String accessToken,
    String tokenType,
    long expiresIn,
    UserRepository user
) {

}
