package com.Market.MeatShop.Security.DTOs.Requests;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(@NotBlank String refreshToken, @NotBlank Long sessionId) {}
