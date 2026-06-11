package com.Market.MeatShop.Security.DTOs.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAuthorityRequest(
    @NotNull @NotBlank(message = "authority cannot be null or empty") String authority) {}
