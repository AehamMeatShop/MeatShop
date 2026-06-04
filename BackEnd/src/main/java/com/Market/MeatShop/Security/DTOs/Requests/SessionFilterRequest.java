package com.Market.MeatShop.Security.DTOs.Requests;

import com.Market.MeatShop.Security.Enums.SecuritySubjectType;
import com.Market.MeatShop.Security.Enums.SessionState;

import java.time.LocalDateTime;

public record SessionFilterRequest(
    SecuritySubjectType partyType,
    Long partyId,
    SessionState state,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Integer trustScore) {}
