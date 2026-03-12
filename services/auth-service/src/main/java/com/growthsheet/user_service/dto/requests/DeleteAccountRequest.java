package com.growthsheet.user_service.dto.requests;

import jakarta.validation.constraints.NotBlank;

public record DeleteAccountRequest(
    @NotBlank String password
) {}