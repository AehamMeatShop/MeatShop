package com.Market.MeatShop.Security.DTOs;

import com.Market.MeatShop.Security.Enums.SessionState;

import java.time.LocalDateTime;

public record SessionViewDto(
    Long id,
    String partyType,
    Long partyId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    SessionState state,
    String baseLineFingerPrint,
    String lastFingerprint,
    Integer trustScore) {}
