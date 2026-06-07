package com.Market.MeatShop.Security.Providers;

import com.Market.MeatShop.Security.Assemblers.SecurityIdentity;
import com.Market.MeatShop.Security.Assemblers.SecuritySubject;
import com.Market.MeatShop.Security.Config.JwtProperties;
import com.Market.MeatShop.Security.Enums.SecuritySubjectType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

  private final JwtProperties jwtProperties;

  // ─── Key ────────────────────────────────────────────────────────────────
  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
  }

  // ─── Generate Access Token ───────────────────────────────────────────────
  public String generateAccessToken(SecurityIdentity secIdentity, Long sessionId) {
    Instant now = Instant.now();
    return Jwts.builder()
        .id(UUID.randomUUID().toString())
        .subject(secIdentity.id().toString())
        .claim("pt", secIdentity.type().name())
        .claim("sid", sessionId)
        .claim("tokenType", "ACCESS")
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plusSeconds(jwtProperties.getAccessTokenExpiry())))
        .signWith(getSigningKey())
        .compact();
  }

  public Claims parseToken(String token) {
    return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
  }

  public boolean isValid(String token) {
    try {
      parseToken(token);
      return true;
    } catch (ExpiredJwtException e) {
      log.warn("Token expired: {}", e.getMessage());
    } catch (JwtException e) {
      log.warn("Invalid token: {}", e.getMessage());
    }
    return false;
  }

  public boolean isExpired(String token) {
    try {
      parseToken(token);
      return false;
    } catch (ExpiredJwtException e) {
      return true;
    } catch (JwtException e) {
      return false;
    }
  }

  // ─── Extractors ──────────────────────────────────────────────────────────
  public Long extractPartyId(String token) {
    return Long.parseLong(parseToken(token).getSubject());
  }

  public SecuritySubjectType extractType(String token) {
    return parseToken(token).get("partyType", SecuritySubjectType.class);
  }

  public Long extractSessionId(String token) {
    return parseToken(token).get("sessionId", Long.class);
  }

  public String extractTokenType(String token) {
    return parseToken(token).get("tokenType", String.class);
  }

  public String extractJti(String token) {
    return parseToken(token).getId();
  }
}
