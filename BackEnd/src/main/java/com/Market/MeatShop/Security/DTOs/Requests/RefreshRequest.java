package com.Market.MeatShop.Security.DTOs.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RefreshRequest(@NotBlank String refreshToken, @NotNull Long sessionId) {}
