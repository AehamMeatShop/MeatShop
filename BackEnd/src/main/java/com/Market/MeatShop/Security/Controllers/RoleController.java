package com.Market.MeatShop.Security.Controllers;

import com.Market.MeatShop.Security.DTOs.Requests.CreateRoleRequest;
import com.Market.MeatShop.Security.DTOs.Requests.UpdateRoleRequest;
import com.Market.MeatShop.Security.DTOs.RoleViewDto;
import com.Market.MeatShop.Security.Services.RoleService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth/roles")
public class RoleController {

  private final RoleService roleService;

  public RoleController(RoleService roleService) {
    this.roleService = roleService;
  }

  @PostMapping
  public ResponseEntity<RoleViewDto> createRole(@Valid @RequestBody CreateRoleRequest request) {
    RoleViewDto createdRole = roleService.createRole(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
  }

  @GetMapping
  public ResponseEntity<List<RoleViewDto>> getAllRoles() {
    List<RoleViewDto> roles = roleService.getAllRoles();
    return ResponseEntity.ok(roles);
  }

  @GetMapping("/{id}")
  public ResponseEntity<RoleViewDto> getRoleById(@PathVariable Long id) {
    RoleViewDto role = roleService.getRoleById(id);
    return ResponseEntity.ok(role);
  }

  @PutMapping("/{id}")
  public ResponseEntity<RoleViewDto> updateRole(
      @PathVariable Long id, @Valid @RequestBody UpdateRoleRequest request) {
    RoleViewDto updatedRole = roleService.updateRole(id, request);
    return ResponseEntity.ok(updatedRole);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
    roleService.deleteRole(id);
    return ResponseEntity.noContent().build();
  }
}
