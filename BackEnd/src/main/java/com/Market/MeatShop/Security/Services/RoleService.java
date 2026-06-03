package com.Market.MeatShop.Security.Services;

import com.Market.MeatShop.Security.DTOs.AuthorityViewDto;
import com.Market.MeatShop.Security.DTOs.PartyRoleViewDto;
import com.Market.MeatShop.Security.DTOs.Requests.AssignAuthorityToRoleRequest;
import com.Market.MeatShop.Security.DTOs.Requests.AssignRoleToPartyRequest;
import com.Market.MeatShop.Security.DTOs.Requests.CreateRoleRequest;
import com.Market.MeatShop.Security.DTOs.Requests.UpdateRoleRequest;
import com.Market.MeatShop.Security.DTOs.RoleViewDto;
import com.Market.MeatShop.Security.Entities.Authority;
import com.Market.MeatShop.Security.Entities.PartyRole;
import com.Market.MeatShop.Security.Entities.Role;
import com.Market.MeatShop.Security.Enums.SecuritySubjectType;
import com.Market.MeatShop.Security.Mappers.PartyRoleMapper;
import com.Market.MeatShop.Security.Mappers.RoleMapper;
import com.Market.MeatShop.Security.Repositories.AuthorityRepo;
import com.Market.MeatShop.Security.Repositories.PartyRoleRepo;
import com.Market.MeatShop.Security.Repositories.RoleAuthorityRepo;
import com.Market.MeatShop.Security.Repositories.RoleRepo;
import com.Market.MeatShop.Shared.Exceptions.TargetNotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoleService {

  private final RoleRepo roleRepo;
  private final RoleMapper roleMapper;
  private final PartyRoleRepo partyRoleRepo;
  private final RoleAuthorityRepo roleAuthorityRepo;
  private final AuthorityRepo authorityRepo;
  private final PartyRoleMapper partyRoleMapper;

  public RoleService(
      RoleRepo roleRepo,
      RoleMapper roleMapper,
      PartyRoleRepo partyRoleRepo,
      RoleAuthorityRepo roleAuthorityRepo,
      AuthorityRepo authorityRepo,
      PartyRoleMapper partyRoleMapper) {
    this.roleRepo = roleRepo;
    this.roleMapper = roleMapper;
    this.partyRoleRepo = partyRoleRepo;
    this.roleAuthorityRepo = roleAuthorityRepo;
    this.authorityRepo = authorityRepo;
    this.partyRoleMapper = partyRoleMapper;
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

  public List<RoleViewDto> getRolesByParty(SecuritySubjectType partyType, Long partyId) {
    List<PartyRole> partyRoles = partyRoleRepo.findByPartyTypeAndPartyId(partyType, partyId);
    return partyRoles.stream()
        .map(PartyRole::getRole)
        .map(roleMapper::toViewDto)
        .collect(Collectors.toList());
  }

  public List<AuthorityViewDto> getAuthoritiesByRole(Long roleId) {
    Role role =
        roleRepo
            .findById(roleId)
            .orElseThrow(() -> new TargetNotFound("Role not found with id: " + roleId));
    return roleAuthorityRepo.findByRole(role).stream()
        .map(
            roleAuthority ->
                new AuthorityViewDto(
                    roleAuthority.getAuthority().getId(),
                    roleAuthority.getAuthority().getAuthority()))
        .collect(Collectors.toList());
  }

  public PartyRoleViewDto assignRoleToParty(AssignRoleToPartyRequest request) {
    Role role =
        roleRepo
            .findById(request.roleId())
            .orElseThrow(() -> new TargetNotFound("Role not found with id: " + request.roleId()));
    PartyRole partyRole = new PartyRole();
    partyRole.setPartyType(request.partyType());
    partyRole.setPartyId(request.partyId());
    partyRole.setRole(role);
    partyRole = partyRoleRepo.save(partyRole);
    log.info("Assigned role {} to party {} of type {}", request.roleId(), request.partyId(), request.partyType());
    return partyRoleMapper.toViewDto(partyRole);
  }

  public void assignAuthorityToRole(AssignAuthorityToRoleRequest request) {
    Role role =
        roleRepo
            .findById(request.roleId())
            .orElseThrow(() -> new TargetNotFound("Role not found with id: " + request.roleId()));
    Authority authority =
        authorityRepo
            .findById(request.authorityId())
            .orElseThrow(() -> new TargetNotFound("Authority not found with id: " + request.authorityId()));
    com.Market.MeatShop.Security.Entities.RoleAuthority roleAuthority =
        new com.Market.MeatShop.Security.Entities.RoleAuthority();
    roleAuthority.setRole(role);
    roleAuthority.setAuthority(authority);
    roleAuthorityRepo.save(roleAuthority);
    log.info("Assigned authority {} to role {}", request.authorityId(), request.roleId());
  }

  public void removeRoleFromParty(
      SecuritySubjectType partyType, Long partyId, Long roleId) {
    PartyRole partyRole =
        partyRoleRepo
            .findByPartyTypeAndPartyIdAndRoleId(partyType, partyId, roleId)
            .orElseThrow(
                () ->
                    new TargetNotFound(
                        "Role not found for party type: "
                            + partyType
                            + ", party id: "
                            + partyId
                            + ", role id: "
                            + roleId));

    partyRoleRepo.delete(partyRole);
    log.info("Removed role {} from party {} of type {}", roleId, partyId, partyType);
  }
}
