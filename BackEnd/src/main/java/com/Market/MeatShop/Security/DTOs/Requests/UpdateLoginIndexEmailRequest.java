package com.Market.MeatShop.Security.DTOs.Requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UpdateLoginIndexEmailRequest(
    @NotNull
    @Email
    String oldEmail,
    @NotNull
    @Email
    String newEmail
) {}
