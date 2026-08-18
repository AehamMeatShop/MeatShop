package com.Market.MeatShop.Security.Services;

import com.Market.MeatShop.Security.Assemblers.SecurityIdentity;
import com.Market.MeatShop.Security.Assemblers.SecuritySubjectFactory;
import com.Market.MeatShop.Security.Assemblers.SecuritySubjectProvider;
import com.Market.MeatShop.Security.Assemblers.SecuritySubjectRegistry;
import com.Market.MeatShop.Security.DTOs.FingerPrint;
import com.Market.MeatShop.Security.DTOs.Requests.CreateAuthorityRequest;
import com.Market.MeatShop.Security.DTOs.Requests.CreateRoleRequest;
import com.Market.MeatShop.Security.DTOs.Requests.LoginRequest;
import com.Market.MeatShop.Security.DTOs.Requests.RefreshRequest;
import com.Market.MeatShop.Security.DTOs.Responses.LoginResponse;
import com.Market.MeatShop.Security.Entities.LoginIndex;
import com.Market.MeatShop.Security.Entities.Session;
import com.Market.MeatShop.Security.Enums.SecurityAuthorities;
import com.Market.MeatShop.Security.Enums.SecuritySubjectType;
import com.Market.MeatShop.Security.Enums.SessionState;
import com.Market.MeatShop.Security.Providers.JwtProvider;
import com.Market.MeatShop.Security.Repositories.LoginIndexRepo;
import com.Market.MeatShop.Security.Repositories.SessionRepo;
import com.Market.MeatShop.Security.SecurityWeb.Dto.AuthContext;
import com.Market.MeatShop.Shared.Exceptions.*;
import com.Market.MeatShop.Utils.SystemAuthorities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class AuthService {
  private final SecuritySubjectRegistry secSubRegestry;

  private final LoginIndexRepo loginIndexRepo;
  private final PasswordEncoder encoder;
  private final FingerPrintService fingerPrintService;
  private final SecureRandom secureRandom = new SecureRandom();
  private final Base64.Encoder Bencoder = Base64.getUrlEncoder().withoutPadding();
  private final JwtProvider jwtProvider;
  private final SessionRepo sessionRepo;
  private final SessionService sessionService;
  private final RoleService roleService;
  private final AuthorityService authorityService;

  public AuthService(
      SecuritySubjectRegistry secSubRegestry,
      LoginIndexRepo loginIndexRepo,
      PasswordEncoder encoder,
      FingerPrintService fingerPrintServic,
      JwtProvider jwtProvider,
      SessionRepo sessionRepo,
      SessionService sessionService,
      RoleService roleService,
      AuthorityService authorityService) {
    this.secSubRegestry = secSubRegestry;
    this.loginIndexRepo = loginIndexRepo;
    this.encoder = encoder;
    this.fingerPrintService = fingerPrintServic;
    this.jwtProvider = jwtProvider;
    this.sessionRepo = sessionRepo;
    this.sessionService = sessionService;
    this.authorityService = authorityService;
    this.roleService = roleService;
  }

  public String generateRefreshToken() {

    byte[] randomBytes = new byte[32];

    secureRandom.nextBytes(randomBytes);

    return Bencoder.encodeToString(randomBytes);
  }

  @Transactional
  public LoginResponse login(LoginRequest loginRequest, AuthContext authContext, String ip) {

    log.info(
        "login via E and P requested  loginRequest : {} loginContext : {}",
        loginRequest,
        authContext);
    Optional<LoginIndex> index = loginIndexRepo.findByEmail(loginRequest.email());
    if (index.isEmpty()) {
      log.warn("login with email {} which is not indexed on the system", loginRequest.email());
      throw new AccountNotFounException(
          "Account with email " + loginRequest.email() + " not indexed yet");
    }

    SecuritySubjectProvider provider = secSubRegestry.getProvider(index.get().getSubjectType());

    SecurityIdentity identity = provider.getSubject(index.get().getSubjectId());

    if (authContext.sid() == null) {
      if (!encoder.matches(loginRequest.password(), identity.password())) {
        log.warn(
            "login field to Account with email {} , account type : {} ",
            loginRequest.email(),
            identity.type());
        throw new LoginFaildException(
            "Account with email " + loginRequest.email() + " Field with wrong password");
      }
      String did = UUID.randomUUID().toString();

      FingerPrint fingerPrint =
          new FingerPrint(
              ip,
              did,
              authContext.screenResolution(),
              authContext.os(),
              authContext.osVersion(),
              authContext.browser());

      String serializedFingerPrint = fingerPrintService.serialize(fingerPrint);

      Session session = new Session();
      session.setBaseLineFingerPrint(serializedFingerPrint);
      session.setPartyId(index.get().getSubjectId());
      session.setPartyType(index.get().getSubjectType());

      String refreshToken = generateRefreshToken();

      String hashedRefToken = encoder.encode(refreshToken);

      session.setRefreshToken(hashedRefToken);

      session.setExpireAt(LocalDateTime.now().plusDays(15));

      session.setLastFingerprint(serializedFingerPrint);

      int trustScore = fingerPrintService.getTrustScore(fingerPrint, fingerPrint);
      session.setTrustScore(trustScore);
      session.setState(fingerPrintService.getSuitableSessionState(trustScore));
      sessionRepo.save(session);
      String accessToken = jwtProvider.generateAccessToken(identity, session.getId());

      LoginResponse response = new LoginResponse(accessToken, refreshToken, did, session.getId());

      log.info("Logging successfully ! with email {}  and password login  ", loginRequest.email());

      return response;
    }

    Session session =
        sessionRepo
            .findByIdAndPartyIdAndPartyType(authContext.sid(), identity.id(), identity.type())
            .orElseThrow(
                () ->
                    new SessionNotFoundException(
                        "session not found for " + authContext.did(), identity));

    sessionService.traceSession(session, identity, authContext, ip);

    if (!encoder.matches(loginRequest.password(), identity.password())) {
      log.warn(
          "login field to Account with email {} , account type : {} ",
          loginRequest.email(),
          identity.type());
      throw new LoginFaildException(
          "Account with email " + loginRequest.email() + " Field with wrong password");
    }

    FingerPrint baseFingerPrint =
        fingerPrintService.toFingerPrint(session.getBaseLineFingerPrint());
    log.info("Base fingerprint: {}", baseFingerPrint);

    FingerPrint contextFingerPrint =
        new FingerPrint(
            ip,
            authContext.did(),
            authContext.screenResolution(),
            authContext.os(),
            authContext.osVersion(),
            authContext.browser());

    int newTrustScore = fingerPrintService.getTrustScore(baseFingerPrint, contextFingerPrint);

    session.setLastFingerprint(fingerPrintService.serialize(contextFingerPrint));

    if (session.getState().equals(SessionState.ACTIVE)
        || session.getState().equals(SessionState.OBSERVED)
        || session.getState().equals(SessionState.CHALLENGED)) {
      boolean shouldIUpdateBFP =
          fingerPrintService.canReplaceBaseline(baseFingerPrint, contextFingerPrint);
      if (shouldIUpdateBFP) {

        log.info(
            """
                Baseline fingerprint updated
                sessionId={}
                oldBaseline={}
                newBaseline={}
                oldQuality={}
                newQuality={}
                """,
            session.getId(),
            fingerPrintService.serialize(baseFingerPrint),
            fingerPrintService.serialize(contextFingerPrint),
            fingerPrintService.getQuality(baseFingerPrint),
            fingerPrintService.getQuality(contextFingerPrint));

        session.setBaseLineFingerPrint(fingerPrintService.serialize(contextFingerPrint));
      }
    }
    session.setState(fingerPrintService.getSuitableSessionState(newTrustScore));
    session.setTrustScore(newTrustScore);
    String refreshToken = generateRefreshToken();
    String hashedRefToken = encoder.encode(refreshToken);
    session.setRefreshToken(hashedRefToken);
    session.setExpireAt(LocalDateTime.now().plusDays(15));
    sessionRepo.save(session);
    String accessToken = jwtProvider.generateAccessToken(identity, session.getId());

    log.info(
        "logging successfully via E and P for the session : {} by trust score : {}",
        session.getId(),
        newTrustScore);
    return new LoginResponse(accessToken, refreshToken, authContext.did(), session.getId());
  }

  public boolean logOut(String accessToken, AuthContext authContext, String ip) {

    Long sessionId = jwtProvider.extractSessionId(accessToken);
    Long partyId = jwtProvider.extractPartyId(accessToken);
    SecuritySubjectType partyType = jwtProvider.extractType(accessToken);

    SecuritySubjectProvider provider = secSubRegestry.getProvider(partyType);

    SecurityIdentity identity = provider.getSubject(partyId);

    Session session =
        sessionRepo
            .findByIdAndPartyIdAndPartyType(sessionId, identity.id(), identity.type())
            .orElseThrow(() -> new SessionNotFoundException("Session not found", identity));

    if (session.getExpireAt().isBefore(LocalDateTime.now())) {
      throw new SessionExpiredException(
          "Session { " + session.getId() + " } expired at : " + session.getExpireAt());
    }

    sessionService.traceSession(session, identity, authContext, ip);
    FingerPrint baseFingerPrint =
        fingerPrintService.toFingerPrint(session.getBaseLineFingerPrint());
    log.info("Base fingerprint: {}", baseFingerPrint);
    FingerPrint contextFingerPrint =
        new FingerPrint(
            ip,
            authContext.did(),
            authContext.screenResolution(),
            authContext.os(),
            authContext.osVersion(),
            authContext.browser());
    log.info("last fingerPrint fingerprint: {}", contextFingerPrint);
    int newTrustScore = fingerPrintService.getTrustScore(baseFingerPrint, contextFingerPrint);

    session.setLastFingerprint(fingerPrintService.serialize(contextFingerPrint));
    session.setTrustScore(newTrustScore);
    if (newTrustScore < 30) {

      log.warn("Suspicious logout attempt — sessionId: {}", session.getId());
    }
    session.setState(SessionState.INACTIVE);
    session.setExpireAt(LocalDateTime.now());

    sessionRepo.save(session);

    return true;
  }

  @Transactional
  public LoginResponse refreshToken(
      RefreshRequest refreshRequest, AuthContext authContext, String ip) {
    log.info("Refresh token requested");

    Session session =
        sessionRepo
            .findById(refreshRequest.sessionId())
            .orElseThrow(() -> new SessionNotFoundException("Invalid refresh token", null));

    if (session.getExpireAt().isBefore(LocalDateTime.now())) {
      throw new SessionExpiredException(
          "Session { " + session.getId() + " } expired at : " + session.getExpireAt());
    }
    if (!encoder.matches(refreshRequest.refreshToken(), session.getRefreshToken())) {
      log.warn("try to refresh the session {} via wrong refresh token", session.getId());
      throw new SessionExpiredException("Invalid refresh token");
    }

    SecuritySubjectProvider provider = secSubRegestry.getProvider(session.getPartyType());
    SecurityIdentity identity = provider.getSubject(session.getPartyId());
    sessionService.traceSession(session, identity, authContext, ip);
    FingerPrint contextFingerPrint =
        new FingerPrint(
            ip,
            authContext.did(),
            authContext.screenResolution(),
            authContext.os(),
            authContext.osVersion(),
            authContext.browser());

    FingerPrint baseFingerPrint =
        fingerPrintService.toFingerPrint(session.getBaseLineFingerPrint());
    int newTrustScore = fingerPrintService.getTrustScore(baseFingerPrint, contextFingerPrint);

    session.setLastFingerprint(fingerPrintService.serialize(contextFingerPrint));
    session.setTrustScore(newTrustScore);
    session.setState(fingerPrintService.getSuitableSessionState(newTrustScore));

    String newRefreshToken = generateRefreshToken();
    String newHashedRefToken = encoder.encode(newRefreshToken);
    session.setRefreshToken(newHashedRefToken);
    session.setExpireAt(LocalDateTime.now().plusDays(15));
    sessionRepo.save(session);

    String accessToken = jwtProvider.generateAccessToken(identity, session.getId());

    log.info("Token refreshed successfully for session: {}", session.getId());
    return new LoginResponse(accessToken, newRefreshToken, authContext.did(), session.getId());
  }

  @Transactional
  public void startSecurityApplication() {
    roleService.createRole(new CreateRoleRequest("SUPER_ADMIN"));
    log.info("SUPER_ADMIN role created");
    List<String> securityAuthorities =
        Arrays.stream(SecurityAuthorities.values()).map(SecurityAuthorities::name).toList();

    for (String authority : securityAuthorities) {
      authorityService.createAuthority(new CreateAuthorityRequest(authority));
    }
    log.info("security authorities created");
  }
}
