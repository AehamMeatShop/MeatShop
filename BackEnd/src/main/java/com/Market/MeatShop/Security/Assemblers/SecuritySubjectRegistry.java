package com.Market.MeatShop.Security.Assemblers;

import com.Market.MeatShop.Security.Enums.SecuritySubjectType;
import com.Market.MeatShop.Shared.Exceptions.SubjectProviderNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SecuritySubjectRegistry {

  private final Map<SecuritySubjectType, SecuritySubjectProvider> providers;

  public SecuritySubjectRegistry(List<SecuritySubjectProvider> providers) {

    if (providers.isEmpty()) {
      throw new IllegalStateException("No SecuritySubjectProviders registered");
    }

    this.providers =
        providers.stream()
            .collect(
                Collectors.toUnmodifiableMap(
                    SecuritySubjectProvider::supports, Function.identity()));

    log.info("Loaded {} SecuritySubjectProviders", this.providers.size());
  }

  public SecuritySubjectProvider getProvider(SecuritySubjectType type) {

    SecuritySubjectProvider provider = providers.get(type);

    if (provider == null) {
      throw new SubjectProviderNotFoundException("Provider not found for type: " + type);
    }

    return provider;
  }
}
