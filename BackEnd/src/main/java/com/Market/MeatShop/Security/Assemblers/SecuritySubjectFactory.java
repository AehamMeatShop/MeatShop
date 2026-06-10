package com.Market.MeatShop.Security.Assemblers;

import com.Market.MeatShop.Security.Services.AuthorityService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SecuritySubjectFactory {
  private final AuthorityService authService;

  public SecuritySubjectFactory(AuthorityService authService) {
    this.authService = authService;
  }

  public SecuritySubject assemble(SecurityIdentity identity) {

    Set<String> stringAuthorities =
        authService.getAllAuthorityNamesForParty(identity.type(), identity.id());
    Set<GrantedAuthority> authorities =
        stringAuthorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    SecuritySubject securitySubject =
        new SecuritySubject(identity.id(), identity.type(), identity.email(), authorities);

    return securitySubject;
  }
}
