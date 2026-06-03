package com.Market.MeatShop.Security.DTOs;

import com.Market.MeatShop.Security.Enums.SecuritySubjectType;

public record PartyRoleViewDto(Long id, Long partyId, SecuritySubjectType partyType, RoleViewDto role) {}
