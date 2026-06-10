package com.Market.MeatShop.Security.Filters;

import com.Market.MeatShop.Security.Assemblers.*;
import com.Market.MeatShop.Security.Entities.Session;
import com.Market.MeatShop.Security.Enums.SecuritySubjectType;
import com.Market.MeatShop.Security.Providers.JwtProvider;
import com.Market.MeatShop.Security.Repositories.SessionRepo;
import com.Market.MeatShop.Security.SecurityWeb.Dto.AuthContext;
import com.Market.MeatShop.Security.Services.SessionService;
import com.Market.MeatShop.Shared.Exceptions.SessionNotFoundException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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
      return;
    }

    token = token.substring(7);

    if (!jwtProvider.isValid(token)) {
      filterChain.doFilter(request, response);
      return;
    }

    SecuritySubjectType PartyType = jwtProvider.extractType(token);
    Long partyId = jwtProvider.extractPartyId(token);
    SecuritySubjectProvider provider = securitySubjectRegistry.getProvider(PartyType);
    SecurityIdentity identity = provider.getSubject(partyId);
    Long sessionId = jwtProvider.extractSessionId(token);
    Session session =
        sessionRepo
            .findById(sessionId)
            .orElseThrow(
                () ->
                    new SessionNotFoundException(
                        "session {" + sessionId + "} not fonud", identity));

    String deviceId = request.getHeader("did");

    String os = request.getHeader("os");

    String osVersion = request.getHeader("osVersion");

    String browser = request.getHeader("browser");

    String screenResolution = request.getHeader("screenResolution");
    String sidHeader = request.getHeader("sid");
    Long sid = (sidHeader != null && !sidHeader.isBlank()) ? Long.valueOf(sidHeader) : null;
    AuthContext authContext =
        new AuthContext(sid, deviceId, os, osVersion, browser, screenResolution);
    sessionService.traceSession(session, identity, authContext, request.getRemoteAddr());

    SecuritySubject securitySubject = securitySubjectFactory.assemble(identity);
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
            securitySubject, null, securitySubject.authorities());

    SecurityContextHolder.getContext().setAuthentication(authentication);
    filterChain.doFilter(request, response);
  }
}
