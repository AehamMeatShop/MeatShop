package com.Market.MeatShop.Security.Assemblers;

import com.Market.MeatShop.Security.Enums.SecuritySubjectType;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

public record SecuritySubject(
    Long id, SecuritySubjectType type, String email, Set<GrantedAuthority> authorities) {}
