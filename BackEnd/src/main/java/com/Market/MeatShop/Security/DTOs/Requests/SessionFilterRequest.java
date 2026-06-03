package com.Market.MeatShop.Security.DTOs.Requests;

import com.Market.MeatShop.Security.Enums.SessionState;

import java.time.LocalDateTime;

public record SessionFilterRequest(
    String partyType,
    Long partyId,
    SessionState state,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Integer trustScore) {}
