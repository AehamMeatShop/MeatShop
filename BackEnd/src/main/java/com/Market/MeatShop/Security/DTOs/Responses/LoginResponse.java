package com.Market.MeatShop.Security.DTOs.Responses;

public record LoginResponse(String token, String refreshToken, String DID, Long sid) {}
