package com.Market.MeatShop.Security.Assemblers;

import com.Market.MeatShop.Security.Services.AuthorityService;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SecuritySubjectFactory {
  private final AuthorityService authService;

  public SecuritySubjectFactory(AuthorityService authService) {
    this.authService = authService;
  }

  public SecuritySubject assemble(SecurityIdentity identity) {

    Set<String> authorities =
        authService.getAllAuthorityNamesForParty(identity.type(), identity.id());

    SecuritySubject securitySubject =
        new SecuritySubject(identity.id(), identity.type(), identity.email(), authorities);

    return securitySubject;
  }
}
