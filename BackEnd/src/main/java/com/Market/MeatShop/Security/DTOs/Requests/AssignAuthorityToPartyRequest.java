package com.Market.MeatShop.Security.DTOs.Requests;

import com.Market.MeatShop.Security.Enums.SecuritySubjectType;
import jakarta.validation.constraints.NotNull;

public record AssignAuthorityToPartyRequest(
    @NotNull(message = "partyType cannot be null") SecuritySubjectType partyType,
    @NotNull(message = "partyId cannot be null") Long partyId,
    @NotNull(message = "authorityId cannot be null") Long authorityId) {}
