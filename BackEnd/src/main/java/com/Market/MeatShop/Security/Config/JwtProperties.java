package com.Market.MeatShop.Security.Config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {
  private String secret;
  private long accessTokenExpiry;
  private long refreshTokenExpiry;
}
