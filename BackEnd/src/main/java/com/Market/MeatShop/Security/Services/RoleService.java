package com.Market.MeatShop.Security.Services;

import com.Market.MeatShop.Security.DTOs.Requests.CreateRoleRequest;
import com.Market.MeatShop.Security.DTOs.Requests.UpdateRoleRequest;
import com.Market.MeatShop.Security.DTOs.RoleViewDto;
import com.Market.MeatShop.Security.Entities.Role;
import com.Market.MeatShop.Security.Mappers.RoleMapper;
import com.Market.MeatShop.Security.Repositories.RoleRepo;
import com.Market.MeatShop.Shared.Exceptions.TargetNotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RoleService {

  private final RoleRepo roleRepo;
  private final RoleMapper roleMapper;

  public RoleService(RoleRepo roleRepo, RoleMapper roleMapper) {
    this.roleRepo = roleRepo;
    this.roleMapper = roleMapper;
  }

  public RoleViewDto createRole(CreateRoleRequest request) {
    Role role = roleMapper.toEntity(request);
    roleRepo.save(role);
    RoleViewDto viewDto = roleMapper.toViewDto(role);
    log.info("new role created {}", viewDto);
    return viewDto;
  }

  public java.util.List<RoleViewDto> getAllRoles() {
    return roleRepo.findAll().stream().map(roleMapper::toViewDto).toList();
  }

  public RoleViewDto getRoleById(Long id) {
    Role role =
        roleRepo
            .findById(id)
            .orElseThrow(() -> new TargetNotFound("Role not found with id: " + id));
    return roleMapper.toViewDto(role);
  }

  public RoleViewDto updateRole(Long id, UpdateRoleRequest request) {
    Role role =
        roleRepo
            .findById(id)
            .orElseThrow(() -> new TargetNotFound("Role not found with id: " + id));
    roleMapper.updateEntityFromDto(request, role);
    roleRepo.save(role);
    RoleViewDto viewDto = roleMapper.toViewDto(role);
    log.info("role updated {}", viewDto);
    return viewDto;
  }

  public void deleteRole(Long id) {
    Role role =
        roleRepo
            .findById(id)
            .orElseThrow(() -> new TargetNotFound("Role not found with id: " + id));
    roleRepo.delete(role);
    log.info("role deleted with id {}", id);
  }

  public RoleViewDto getRoleByName(String name) {
    Role role =
        roleRepo
            .findByName(name)
            .orElseThrow(() -> new TargetNotFound("Role not found with name: " + name));
    return roleMapper.toViewDto(role);
  }
}
