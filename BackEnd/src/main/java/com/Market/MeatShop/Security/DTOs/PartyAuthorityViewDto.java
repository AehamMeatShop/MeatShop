package com.Market.MeatShop.Security.DTOs;

public record PartyAuthorityViewDto(Long id, Long partyId, String partyType, AuthorityViewDto authority) {}
