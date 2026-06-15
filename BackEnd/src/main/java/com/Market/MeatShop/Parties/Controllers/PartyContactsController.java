package com.Market.MeatShop.Parties.Controllers;

import com.Market.MeatShop.Parties.DTOs.Requests.CreatePartyContactReq;
import com.Market.MeatShop.Parties.DTOs.Requests.CreatePartyRequest;
import com.Market.MeatShop.Parties.DTOs.Requests.UpdatePartyContactReq;
import com.Market.MeatShop.Parties.Entities.PartyContact;
import com.Market.MeatShop.Parties.Services.PartyContactService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/parties/contacts")
@Slf4j
public class PartyContactsController {
  private PartyContactService partyContactService;

  public PartyContactsController(PartyContactService partyContactService) {
    this.partyContactService = partyContactService;
  }

  @PreAuthorize("permitAll()")
  @GetMapping("/health-check")
  public ResponseEntity<?> healthCheck() {
    log.info("GET /parties/contacts/health-check requested");
    return ResponseEntity.ok().build();
  }

  @PreAuthorize("hasAuthority('PARTY_MANAGEMENT')")
  @PostMapping
  public ResponseEntity<?> createPartyContact(@Valid @RequestBody CreatePartyContactReq req) {
    log.info("POST /parties/contacts {} requested", req);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(partyContactService.createPartyContact(req));
  }

  @PreAuthorize("hasAuthority('PARTY_MANAGEMENT')")
  @PutMapping("/{id}")
  public ResponseEntity<?> updatePartyContact(
      @Valid @RequestBody UpdatePartyContactReq req, @PathVariable Long id) {
    log.info("PUT /parties/contacts/{} {} requested", id, req);
    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body(partyContactService.updatePartyContact(req, id));
  }

  @PreAuthorize("hasAuthority('PARTY_MANAGEMENT')")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deletePartyContact(@PathVariable Long id) {
    log.info("DELETE /parties/contacts/{} requested", id);
    partyContactService.deletePartyContact(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
