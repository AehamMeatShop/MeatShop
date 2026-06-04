package com.Market.MeatShop.Security.DTOs.Requests;

import com.Market.MeatShop.Security.Enums.SecuritySubjectType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record CreateLoginIndexRequest(
    @NotNull Long subjectId,
    @NotNull SecuritySubjectType subjectType,
    @NotNull
    @Email
    String email
) {}
