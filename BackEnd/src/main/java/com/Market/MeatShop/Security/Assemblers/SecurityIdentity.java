package com.Market.MeatShop.Security.Assemblers;

import com.Market.MeatShop.Security.Enums.SecuritySubjectType;

public record SecurityIdentity(Long id, SecuritySubjectType type, String email, String password) {}
