package com.Market.MeatShop.Security.DTOs.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateRoleRequest(
    @NotNull @NotBlank(message = "name of role cannot be null or empty") String name) {}
