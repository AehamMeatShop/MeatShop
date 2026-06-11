package com.Market.MeatShop.Security.Controllers;

import com.Market.MeatShop.Security.DTOs.Requests.SessionFilterRequest;
import com.Market.MeatShop.Security.DTOs.SessionViewDto;
import com.Market.MeatShop.Security.Enums.SessionState;
import com.Market.MeatShop.Security.Services.SessionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth/sessions")
public class SessionController {

  private final SessionService sessionService;

  public SessionController(SessionService sessionService) {
    this.sessionService = sessionService;
  }

  @PreAuthorize("hasAuthority('SESSION_MANAGEMENT')")
  @GetMapping
  public ResponseEntity<Page<SessionViewDto>> getSessions(
      SessionFilterRequest filter, Pageable pageable) {
    Page<SessionViewDto> sessions = sessionService.findAllByFilter(filter, pageable);
    return ResponseEntity.ok(sessions);
  }

  @PreAuthorize("hasAuthority('SESSION_MANAGEMENT')")
  @PatchMapping("/{id}/state")
  public ResponseEntity<SessionViewDto> updateSessionState(
      @PathVariable Long id, @RequestParam SessionState state) {
    SessionViewDto session = sessionService.updateSessionState(id, state);
    return ResponseEntity.ok(session);
  }
}
