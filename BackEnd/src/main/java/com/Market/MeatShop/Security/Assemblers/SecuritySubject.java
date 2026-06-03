package com.Market.MeatShop.Security.Assemblers;

import com.Market.MeatShop.Security.Enums.SecuritySubjectType;

import java.util.Set;

public record SecuritySubject(
    Long id, SecuritySubjectType type, String email, Set<String> authorities) {}
