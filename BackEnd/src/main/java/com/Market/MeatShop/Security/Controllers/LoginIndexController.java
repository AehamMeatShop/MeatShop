package com.Market.MeatShop.Security.Controllers;

import com.Market.MeatShop.Security.DTOs.Requests.CreateLoginIndexRequest;
import com.Market.MeatShop.Security.DTOs.Requests.UpdateLoginIndexEmailRequest;
import com.Market.MeatShop.Security.Services.LoginIndexService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/login-indexes")
@Slf4j
public class LoginIndexController {

  private final LoginIndexService loginIndexService;

  public LoginIndexController(LoginIndexService loginIndexService) {
    this.loginIndexService = loginIndexService;
  }

  @PostMapping
  public ResponseEntity<Boolean> createIndex(@Valid @RequestBody CreateLoginIndexRequest req) {
    log.info("POST /auth/login-indexes requested with email: {}", req.email());
    boolean result = loginIndexService.createIndex(req.subjectId(), req.subjectType(), req.email());
    return ResponseEntity.ok(result);
  }

  @PutMapping("/email")
  public ResponseEntity<Boolean> updateEmail(@Valid @RequestBody UpdateLoginIndexEmailRequest req) {
    log.info(
        "PUT /auth/login-indexes/email requested from {} to {}", req.oldEmail(), req.newEmail());
    boolean result = loginIndexService.updateEmail(req.oldEmail(), req.newEmail());
    return ResponseEntity.ok(result);
  }
}
