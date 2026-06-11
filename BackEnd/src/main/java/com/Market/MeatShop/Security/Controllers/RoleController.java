package com.Market.MeatShop.Security.Controllers;

import com.Market.MeatShop.Security.DTOs.AuthorityViewDto;
import com.Market.MeatShop.Security.DTOs.PartyRoleViewDto;
import com.Market.MeatShop.Security.DTOs.Requests.AssignAuthorityToRoleRequest;
import com.Market.MeatShop.Security.DTOs.Requests.AssignRoleToPartyRequest;
import com.Market.MeatShop.Security.DTOs.Requests.CreateRoleRequest;
import com.Market.MeatShop.Security.DTOs.Requests.UpdateRoleRequest;
import com.Market.MeatShop.Security.DTOs.RoleViewDto;
import com.Market.MeatShop.Security.Enums.SecuritySubjectType;
import com.Market.MeatShop.Security.Services.RoleService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth/roles")
public class RoleController {

  private final RoleService roleService;

  public RoleController(RoleService roleService) {
    this.roleService = roleService;
  }

  @PreAuthorize("hasAuthority('ROLE_MANAGEMENT')")
  @PostMapping
  public ResponseEntity<RoleViewDto> createRole(@Valid @RequestBody CreateRoleRequest request) {
    RoleViewDto createdRole = roleService.createRole(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
  }

  @PreAuthorize("hasAuthority('ROLE_MANAGEMENT')")
  @GetMapping
  public ResponseEntity<List<RoleViewDto>> getAllRoles() {
    List<RoleViewDto> roles = roleService.getAllRoles();
    return ResponseEntity.ok(roles);
  }

  @PreAuthorize("hasAuthority('ROLE_MANAGEMENT')")
  @GetMapping("/{id}")
  public ResponseEntity<RoleViewDto> getRoleById(@PathVariable Long id) {
    RoleViewDto role = roleService.getRoleById(id);
    return ResponseEntity.ok(role);
  }

  @PreAuthorize("hasAuthority('ROLE_MANAGEMENT')")
  @PutMapping("/{id}")
  public ResponseEntity<RoleViewDto> updateRole(
      @PathVariable Long id, @Valid @RequestBody UpdateRoleRequest request) {
    RoleViewDto updatedRole = roleService.updateRole(id, request);
    return ResponseEntity.ok(updatedRole);
  }

  @PreAuthorize("hasAuthority('ROLE_MANAGEMENT')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
    roleService.deleteRole(id);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasAuthority('ROLE_MANAGEMENT')")
  @GetMapping("/party")
  public ResponseEntity<List<RoleViewDto>> getRolesByParty(
      @RequestParam SecuritySubjectType partyType, @RequestParam Long partyId) {
    List<RoleViewDto> roles = roleService.getRolesByParty(partyType, partyId);
    return ResponseEntity.ok(roles);
  }

  @PreAuthorize("hasAuthority('ROLE_MANAGEMENT')")
  @GetMapping("/{roleId}/authorities")
  public ResponseEntity<List<AuthorityViewDto>> getAuthoritiesByRole(@PathVariable Long roleId) {
    List<AuthorityViewDto> authorities = roleService.getAuthoritiesByRole(roleId);
    return ResponseEntity.ok(authorities);
  }

  @PreAuthorize("hasAuthority('ROLE_MANAGEMENT')")
  @PostMapping("/assign-to-party")
  public ResponseEntity<PartyRoleViewDto> assignRoleToParty(
      @Valid @RequestBody AssignRoleToPartyRequest request) {
    PartyRoleViewDto partyRole = roleService.assignRoleToParty(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(partyRole);
  }

  @PreAuthorize("hasAuthority('ROLE_MANAGEMENT')")
  @PostMapping("/{roleId}/authorities/{authorityId}")
  public ResponseEntity<Void> assignAuthorityToRole(
      @PathVariable Long roleId, @PathVariable Long authorityId) {
    roleService.assignAuthorityToRole(new AssignAuthorityToRoleRequest(roleId, authorityId));
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PreAuthorize("hasAuthority('ROLE_MANAGEMENT')")
  @DeleteMapping("/remove-from-party")
  public ResponseEntity<Void> removeRoleFromParty(
      @RequestParam SecuritySubjectType partyType,
      @RequestParam Long partyId,
      @RequestParam Long roleId) {
    roleService.removeRoleFromParty(partyType, partyId, roleId);
    return ResponseEntity.noContent().build();
  }
}
