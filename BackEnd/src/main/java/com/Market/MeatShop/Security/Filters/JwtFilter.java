package com.Market.MeatShop.Security.Filters;

import com.Market.MeatShop.Security.Assemblers.*;
import com.Market.MeatShop.Security.Entities.Session;
import com.Market.MeatShop.Security.Enums.SecuritySubjectType;
import com.Market.MeatShop.Security.Enums.SessionState;
import com.Market.MeatShop.Security.Providers.JwtProvider;
import com.Market.MeatShop.Security.Repositories.SessionRepo;
import com.Market.MeatShop.Security.SecurityWeb.Dto.AuthContext;
import com.Market.MeatShop.Security.Services.SessionService;
import com.Market.MeatShop.Shared.Exceptions.SessionNotFoundException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {
  private final SecuritySubjectRegistry securitySubjectRegistry;
  private final SessionService sessionService;
  private final JwtProvider jwtProvider;
  private final SecuritySubjectFactory securitySubjectFactory;
  private final SessionRepo sessionRepo;

  public JwtFilter(
      SecuritySubjectRegistry securitySubjectRegistry,
      SessionService sessionService,
      JwtProvider jwtProvider,
      SecuritySubjectFactory securitySubjectFactory,
      SessionRepo sessionRepo) {
    this.securitySubjectRegistry = securitySubjectRegistry;
    this.sessionService = sessionService;
    this.jwtProvider = jwtProvider;
    this.securitySubjectFactory = securitySubjectFactory;
    this.sessionRepo = sessionRepo;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String token = request.getHeader("Authorization");

    if (token == null || !token.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      log.info("No token found or invalid token");
      return;
    }

    token = token.substring(7);

    if (!jwtProvider.isValid(token)) {
      filterChain.doFilter(request, response);
      log.info("jwt token is invalid");
      return;
    }

    SecuritySubjectType PartyType = jwtProvider.extractType(token);
    Long partyId = jwtProvider.extractPartyId(token);
    SecuritySubjectProvider provider = securitySubjectRegistry.getProvider(PartyType);
    SecurityIdentity identity = provider.getSubject(partyId);
    Long sessionId = jwtProvider.extractSessionId(token);

    SecuritySubject securitySubject = securitySubjectFactory.assemble(identity);
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
            securitySubject, sessionId, securitySubject.authorities());

    SecurityContextHolder.getContext().setAuthentication(authentication);
    log.info("the security subject inter to the context subject {}", securitySubject);
    filterChain.doFilter(request, response);

    log.info("continue the filter chain after jwt filter ");
  }
}
