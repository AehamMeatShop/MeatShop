package com.Market.MeatShop.Security.DTOs.Requests;

import jakarta.validation.constraints.NotNull;

public record AssignAuthorityToRoleRequest(
    @NotNull(message = "roleId cannot be null") Long roleId,
    @NotNull(message = "authorityId cannot be null") Long authorityId) {}
