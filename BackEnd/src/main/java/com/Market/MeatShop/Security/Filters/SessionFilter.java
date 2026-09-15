package com.Market.MeatShop.Security.Filters;

import com.Market.MeatShop.Security.Assemblers.SecurityIdentity;
import com.Market.MeatShop.Security.Assemblers.SecuritySubject;

import com.Market.MeatShop.Security.Entities.Session;
import com.Market.MeatShop.Security.Enums.SessionState;

import com.Market.MeatShop.Security.Repositories.SessionRepo;
import com.Market.MeatShop.Security.SecurityWeb.Dto.AuthContext;
import com.Market.MeatShop.Security.Services.SessionService;
import com.Market.MeatShop.Shared.Exceptions.SessionNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
public class SessionFilter extends OncePerRequestFilter {

  private final SessionService sessionService;

  private final SessionRepo sessionRepo;

  public SessionFilter(SessionService sessionService, SessionRepo sessionRepo) {
    this.sessionService = sessionService;

    this.sessionRepo = sessionRepo;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String uri = request.getRequestURI();
    if (SecurityContextHolder.getContext().getAuthentication()
            instanceof AnonymousAuthenticationToken
        || SecurityContextHolder.getContext().getAuthentication() == null
        || !uri.contains("login")
        || !uri.contains("refresh")
        || !uri.contains("log-out")
        || !uri.contains("auth")) {

      return true;
    }
    return false;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    UsernamePasswordAuthenticationToken authentication =
        (UsernamePasswordAuthenticationToken)
            SecurityContextHolder.getContext().getAuthentication();

    SecuritySubject subject = (SecuritySubject) authentication.getPrincipal();
    Long sessionId = (Long) authentication.getCredentials();
    log.info("session filter see the Session id: {}", sessionId);
    Session session =
        sessionRepo
            .findById(sessionId)
            .orElseThrow(
                () -> new SessionNotFoundException("session {" + sessionId + "} not found"));

    String deviceId = request.getHeader("did");

    String os = request.getHeader("os");

    String osVersion = request.getHeader("osVersion");

    String browser = request.getHeader("browser");

    String screenResolution = request.getHeader("screenResolution");

    AuthContext authContext =
        new AuthContext(sessionId, deviceId, os, osVersion, browser, screenResolution);
    sessionService.traceSession(
        session,
        new SecurityIdentity(subject.id(), subject.type(), subject.email(), null),
        authContext,
        request.getRemoteAddr());
    if (session.getState().equals(SessionState.INACTIVE)
        || session.getExpireAt().isBefore(LocalDateTime.now())) {

      log.info("session is inactive or expired");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    filterChain.doFilter(request, response);
  }
}
