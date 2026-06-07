package com.Market.MeatShop.Security.Config;

import com.Market.MeatShop.Security.SecurityWeb.Resolvers.AuthContextResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.util.List;

@Configuration
public class SecurityWebConfig implements WebMvcConfigurer {

  private final AuthContextResolver resolver;

  public SecurityWebConfig(AuthContextResolver resolver) {
    this.resolver = resolver;
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {

    resolvers.add(resolver);
  }
}
