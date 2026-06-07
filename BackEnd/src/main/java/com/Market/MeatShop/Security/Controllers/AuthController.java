package com.Market.MeatShop.Security.Controllers;

import com.Market.MeatShop.Security.DTOs.Requests.LoginRequest;
import com.Market.MeatShop.Security.SecurityWeb.Dto.AuthContext;
import com.Market.MeatShop.Security.Services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jdk.jfr.Registered;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(
      @RequestBody @Valid LoginRequest loginRequest,
      AuthContext authContext,
      HttpServletRequest request) {
    String ip = request.getRemoteAddr();

    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body(authService.login(loginRequest, authContext, ip));
  }
}
