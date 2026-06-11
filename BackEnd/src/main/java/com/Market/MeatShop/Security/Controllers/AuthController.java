package com.Market.MeatShop.Security.Controllers;

import com.Market.MeatShop.Security.DTOs.Requests.LoginRequest;
import com.Market.MeatShop.Security.DTOs.Requests.RefreshRequest;
import com.Market.MeatShop.Security.SecurityWeb.Dto.AuthContext;
import com.Market.MeatShop.Security.Services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jdk.jfr.Registered;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/log-out")
  public ResponseEntity<?> logOut(HttpServletRequest request, AuthContext authContext) {
    String ip = request.getRemoteAddr();
    String authHeader = request.getHeader("Authorization");

    // Extract pure token from "Bearer <token>"
    String token = null;
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      token = authHeader.substring(7);
    }
    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body(authService.logOut(token, authContext, ip));
  }

  @PostMapping("/refresh")
  public ResponseEntity<?> refresh(
      @RequestBody @Valid RefreshRequest refreshRequest,
      AuthContext authContext,
      HttpServletRequest request) {
    String ip = request.getRemoteAddr();
    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body(authService.refreshToken(refreshRequest, authContext, ip));
  }
}
