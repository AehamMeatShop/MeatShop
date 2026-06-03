package com.Market.MeatShop.Security.DTOs.Requests;

import jakarta.validation.constraints.NotNull;

public record GetAuthoritiesByRoleRequest(
    @NotNull(message = "roleId cannot be null") Long roleId) {}
