package com.Market.MeatShop.Security.Controllers;

import com.Market.MeatShop.Security.DTOs.AuthorityViewDto;
import com.Market.MeatShop.Security.DTOs.PartyAuthorityViewDto;
import com.Market.MeatShop.Security.DTOs.Requests.AssignAuthorityToPartyRequest;
import com.Market.MeatShop.Security.Enums.SecuritySubjectType;
import com.Market.MeatShop.Security.Services.AuthorityService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth/authorities")
public class AuthorityController {

  private final AuthorityService authorityService;

  public AuthorityController(AuthorityService authorityService) {
    this.authorityService = authorityService;
  }

  @GetMapping("/party")
  public ResponseEntity<?> getAuthoritiesByParty(
      @RequestParam SecuritySubjectType partyType, @RequestParam Long partyId) {

    return ResponseEntity.ok(authorityService.getAuthoritiesByParty(partyType, partyId));
  }

  @PostMapping("/assign-to-party")
  public ResponseEntity<PartyAuthorityViewDto> assignAuthorityToParty(
      @Valid @RequestBody AssignAuthorityToPartyRequest request) {
    PartyAuthorityViewDto partyAuthority = authorityService.assignAuthorityToParty(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(partyAuthority);
  }

  @DeleteMapping("/remove-from-party")
  public ResponseEntity<Void> removeAuthorityFromParty(
      @RequestParam SecuritySubjectType partyType,
      @RequestParam Long partyId,
      @RequestParam Long authorityId) {
    authorityService.removeAuthorityFromParty(partyType, partyId, authorityId);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/remove-from-role")
  public ResponseEntity<Void> removeAuthorityFromRole(
      @RequestParam Long roleId, @RequestParam Long authorityId) {
    authorityService.removeAuthorityFromRole(roleId, authorityId);
    return ResponseEntity.noContent().build();
  }
}
