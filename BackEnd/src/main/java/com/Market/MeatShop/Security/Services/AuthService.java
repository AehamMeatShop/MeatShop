package com.Market.MeatShop.Security.Services;

import com.Market.MeatShop.Security.Assemblers.SecurityIdentity;
import com.Market.MeatShop.Security.Assemblers.SecuritySubjectFactory;
import com.Market.MeatShop.Security.Assemblers.SecuritySubjectProvider;
import com.Market.MeatShop.Security.Assemblers.SecuritySubjectRegistry;
import com.Market.MeatShop.Security.DTOs.FingerPrint;
import com.Market.MeatShop.Security.DTOs.Requests.LoginRequest;
import com.Market.MeatShop.Security.DTOs.Responses.LoginResponse;
import com.Market.MeatShop.Security.Entities.LoginIndex;
import com.Market.MeatShop.Security.Entities.Session;
import com.Market.MeatShop.Security.Enums.SessionState;
import com.Market.MeatShop.Security.Providers.JwtProvider;
import com.Market.MeatShop.Security.Repositories.LoginIndexRepo;
import com.Market.MeatShop.Security.Repositories.SessionRepo;
import com.Market.MeatShop.Security.SecurityWeb.Dto.AuthContext;
import com.Market.MeatShop.Shared.Exceptions.AccountNotFounException;
import com.Market.MeatShop.Shared.Exceptions.LoginFaildException;
import com.Market.MeatShop.Shared.Exceptions.SessionStolenException;
import com.Market.MeatShop.Shared.Exceptions.SessionNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class AuthService {
  private final SecuritySubjectRegistry secSubRegestry;
  private final SecuritySubjectFactory secSubFactory;
  private final LoginIndexRepo loginIndexRepo;
  private final PasswordEncoder encoder;
  private final FingerPrintService fingerPrintService;
  private final SecureRandom secureRandom = new SecureRandom();
  private final Base64.Encoder Bencoder = Base64.getUrlEncoder().withoutPadding();
  private final JwtProvider jwtProvider;
  private final SessionRepo sessionRepo;

  public AuthService(
      SecuritySubjectRegistry secSubRegestry,
      SecuritySubjectFactory secSubFactory,
      LoginIndexRepo loginIndexRepo,
      PasswordEncoder encoder,
      FingerPrintService fingerPrintServic,
      JwtProvider jwtProvider,
      SessionRepo sessionRepo) {
    this.secSubRegestry = secSubRegestry;
    this.secSubFactory = secSubFactory;
    this.loginIndexRepo = loginIndexRepo;
    this.encoder = encoder;
    this.fingerPrintService = fingerPrintServic;
    this.jwtProvider = jwtProvider;
    this.sessionRepo = sessionRepo;
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
          "Account with email " + loginRequest.email() + " not found");
    }

    SecuritySubjectProvider provider = secSubRegestry.getProvider(index.get().getSubjectType());

    SecurityIdentity identity = provider.getSubject(index.get().getSubjectId());

    if (authContext.did() == null || authContext.did().isEmpty()) {
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

      LoginResponse response = new LoginResponse(accessToken, refreshToken, did);

      log.info("Logging successfully ! with email {}  and password login  ", loginRequest.email());

      return response;
    }

    Session session =
        sessionRepo
            .findFirstByBaseLineFingerPrintContainingAndPartyIdAndPartyType(
                authContext.did(), identity.id(), identity.type())
            .orElseThrow(
                () ->
                    new SessionNotFoundException(
                        "session not found for " + authContext.did(), identity));
    if (session.getState().equals(SessionState.OBSERVED)
        || session.getState().equals(SessionState.CHALLENGED)) {
      log.info(
          "the session : {} is : {} and try to login via E : {} and P",
          session.getId(),
          session.getState(),
          identity.email());
    } else if (session.getState().equals(SessionState.STOLEN)) {
      log.info(
          "the session : {} is : {} and try to login via E : {} and P",
          session.getId(),
          session.getState(),
          identity.email());

      throw new SessionStolenException("try to access to stolen session   ", identity);
    }

    if (!encoder.matches(loginRequest.password(), identity.password())) {
      log.warn(
          "login field to Account with email {} , account type : {} ",
          loginRequest.email(),
          identity.type());
      throw new LoginFaildException(
          "Account with email " + loginRequest.email() + " Field with wrong password");
    }
    if (session.getState().equals(SessionState.REVOKED)) {
      log.info(
          "the session : {} is : {} and try to login via E : {} and P",
          session.getId(),
          session.getState(),
          identity.email());
      throw new LoginFaildException(
          "the session { "
              + session.getId()
              + " } with email { "
              + loginRequest.email()
              + "} is revoked ");
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
        session.setBaseLineFingerPrint(fingerPrintService.serialize(contextFingerPrint));
        log.info(
            "fingerprint  for session : {} will updated : {} trust score : {}",
            session.getId(),
            fingerPrintService.serialize(contextFingerPrint),
            newTrustScore);
      }
    }
    session.setState(fingerPrintService.getSuitableSessionState(newTrustScore));
    session.setTrustScore(newTrustScore);
    String refreshToken = generateRefreshToken();
    String hashedRefToken = encoder.encode(refreshToken);
    session.setRefreshToken(hashedRefToken);

    sessionRepo.save(session);
    String accessToken = jwtProvider.generateAccessToken(identity, session.getId());
    log.info(
        "logging successfully via E and P for the session : {} by trust score : {}",
        session.getId(),
        newTrustScore);
    return new LoginResponse(accessToken, session.getRefreshToken(), authContext.did());
  }
}
