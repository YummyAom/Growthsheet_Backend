package com.growthsheet.user_service.dto.requests;

public record VerifyOtpRequest(
    String email,
    String otp
) {}
