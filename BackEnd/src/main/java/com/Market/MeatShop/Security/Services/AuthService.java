package com.Market.MeatShop.Security.Services;

import com.Market.MeatShop.Security.Assemblers.SecurityIdentity;
import com.Market.MeatShop.Security.Assemblers.SecuritySubjectFactory;
import com.Market.MeatShop.Security.Assemblers.SecuritySubjectProvider;
import com.Market.MeatShop.Security.Assemblers.SecuritySubjectRegistry;
import com.Market.MeatShop.Security.DTOs.Requests.LoginRequest;
import com.Market.MeatShop.Security.DTOs.Responses.LoginResponse;
import com.Market.MeatShop.Security.Entities.LoginIndex;
import com.Market.MeatShop.Security.Repositories.LoginIndexRepo;
import com.Market.MeatShop.Shared.Exceptions.AccountNotFounException;
import com.Market.MeatShop.Shared.Exceptions.LoginFaildException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AuthService {
  private final SecuritySubjectRegistry secSubRegestry;
  private final SecuritySubjectFactory secSubFactory;
  private final LoginIndexRepo loginIndexRepo;
  private final PasswordEncoder encoder;
  private final FingerPrintService fingerPrintService;

  public AuthService(
      SecuritySubjectRegistry secSubRegestry,
      SecuritySubjectFactory secSubFactory,
      LoginIndexRepo loginIndexRepo,
      PasswordEncoder encoder,
      FingerPrintService fingerPrintServic) {
    this.secSubRegestry = secSubRegestry;
    this.secSubFactory = secSubFactory;
    this.loginIndexRepo = loginIndexRepo;
    this.encoder = encoder;
    this.fingerPrintService = fingerPrintServic;
  }

  public LoginResponse login(LoginRequest loginRequest) {
    Optional<LoginIndex> index = loginIndexRepo.findByEmail(loginRequest.email());
    if (index.isEmpty()) {
      log.warn("login with email {} which is not indexed on the system", loginRequest.email());
      throw new AccountNotFounException(
          "Account with email " + loginRequest.email() + " not found");
    }

    SecuritySubjectProvider provider = secSubRegestry.getProvider(index.get().getSubjectType());

    SecurityIdentity identity = provider.getSubject(index.get().getSubjectId());

    if (!encoder.matches(loginRequest.password(), identity.password())) {
      log.warn(
          "login field to Account with email {} , account type : {} ",
          loginRequest.email(),
          identity.type());
      throw new LoginFaildException(
          "Account with email " + loginRequest.email() + " Field with wrong password");
    }
    return null;
  }
}
