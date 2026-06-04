package com.Market.MeatShop.Security.Services;

import com.Market.MeatShop.Security.DTOs.AuthorityViewDto;
import com.Market.MeatShop.Security.DTOs.PartyAuthorityViewDto;
import com.Market.MeatShop.Security.DTOs.Requests.AssignAuthorityToPartyRequest;
import com.Market.MeatShop.Security.Entities.Authority;
import com.Market.MeatShop.Security.Entities.PartyAuthority;
import com.Market.MeatShop.Security.Entities.RoleAuthority;
import com.Market.MeatShop.Security.Enums.SecuritySubjectType;
import com.Market.MeatShop.Security.Mappers.AuthorityMapper;
import com.Market.MeatShop.Security.Mappers.PartyAuthorityMapper;
import com.Market.MeatShop.Security.Repositories.*;
import com.Market.MeatShop.Shared.Exceptions.TargetNotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthorityService {

  private final PartyAuthorityRepo partyAuthorityRepo;
  private final AuthorityRepo authorityRepo;
  private final PartyAuthorityMapper partyAuthorityMapper;
  private final PartyRoleRepo partyRoleRepo;
  private final RoleAuthorityRepo roleAuthorityRepo;
  private final RoleRepo roleRepo;
  private final AuthorityMapper authorityMapper;

  public AuthorityService(
      PartyAuthorityRepo partyAuthorityRepo,
      AuthorityRepo authorityRepo,
      PartyAuthorityMapper partyAuthorityMapper,
      PartyRoleRepo partyRoleRepo,
      RoleAuthorityRepo roleAuthorityRepo,
      RoleRepo roleRepo,
      AuthorityMapper authorityMapper) {
    this.partyAuthorityRepo = partyAuthorityRepo;
    this.authorityRepo = authorityRepo;
    this.partyAuthorityMapper = partyAuthorityMapper;
    this.partyRoleRepo = partyRoleRepo;
    this.roleAuthorityRepo = roleAuthorityRepo;
    this.roleRepo = roleRepo;
    this.authorityMapper = authorityMapper;
  }

  public List<AuthorityViewDto> getAuthoritiesByParty(SecuritySubjectType partyType, Long partyId) {
    List<AuthorityViewDto> allAuthorities = new ArrayList<>();

    List<com.Market.MeatShop.Security.Entities.PartyRole> partyRoles =
        partyRoleRepo.findByPartyTypeAndPartyId(partyType, partyId);

    if (!partyRoles.isEmpty()) {
      boolean isSuperAdmin =
          partyRoles.stream().map(pr -> pr.getRole().getName()).anyMatch("SUPER_ADMIN"::equals);

      if (isSuperAdmin) {
        return authorityMapper.toListViewDto(authorityRepo.findAll());
      }
      List<Long> roleIds =
          partyRoles.stream()
              .map(partyRole -> partyRole.getRole().getId())
              .collect(Collectors.toList());

      List<Authority> roleAuthorities = roleAuthorityRepo.findAuthorityByRoleIds(roleIds);
      allAuthorities.addAll(authorityMapper.toListViewDto(roleAuthorities));
    }

    List<Authority> directPartyAuthorities =
        partyAuthorityRepo.findAuthorityByPartyTypeAndPartyId(partyType, partyId);
    allAuthorities.addAll(authorityMapper.toListViewDto(directPartyAuthorities));

    return allAuthorities;
  }

  public PartyAuthorityViewDto assignAuthorityToParty(AssignAuthorityToPartyRequest request) {
    Authority authority =
        authorityRepo
            .findById(request.authorityId())
            .orElseThrow(
                () -> new TargetNotFound("Authority not found with id: " + request.authorityId()));
    PartyAuthority partyAuthority = new PartyAuthority();
    partyAuthority.setPartyType(request.partyType());
    partyAuthority.setPartyId(request.partyId());
    partyAuthority.setAuthority(authority);
    partyAuthority = partyAuthorityRepo.save(partyAuthority);
    log.info(
        "Assigned authority {} to party {} of type {}",
        request.authorityId(),
        request.partyId(),
        request.partyType());
    return partyAuthorityMapper.toViewDto(partyAuthority);
  }

  public Set<String> getAllAuthorityNamesForParty(SecuritySubjectType partyType, Long partyId) {
    List<AuthorityViewDto> authorities = getAuthoritiesByParty(partyType, partyId);

    return authorities.stream().map(AuthorityViewDto::authority).collect(Collectors.toSet());
  }

  public void removeAuthorityFromParty(
      SecuritySubjectType partyType, Long partyId, Long authorityId) {
    PartyAuthority partyAuthority =
        partyAuthorityRepo
            .findByPartyTypeAndPartyIdAndAuthorityId(partyType, partyId, authorityId)
            .orElseThrow(
                () ->
                    new TargetNotFound(
                        "Authority not found for party type: "
                            + partyType
                            + ", party id: "
                            + partyId
                            + ", authority id: "
                            + authorityId));
    partyAuthorityRepo.delete(partyAuthority);
    log.info("Removed authority {} from party {} of type {}", authorityId, partyId, partyType);
  }

  public void removeAuthorityFromRole(Long roleId, Long authorityId) {
    RoleAuthority roleAuthority =
        roleAuthorityRepo
            .findByRoleIdAndAuthorityId(roleId, authorityId)
            .orElseThrow(
                () ->
                    new TargetNotFound(
                        "Authority not found for role id: "
                            + roleId
                            + ", authority id: "
                            + authorityId));

    roleAuthorityRepo.delete(roleAuthority);
    log.info("Removed authority {} from role {}", authorityId, roleId);
  }

  public void removeAllAuthoritiesForParty(SecuritySubjectType partyType, Long partyId) {
    List<PartyAuthority> partyAuthorities = 
        partyAuthorityRepo.findByPartyTypeAndPartyId(partyType, partyId);
    partyAuthorityRepo.deleteAll(partyAuthorities);
    log.info("Removed all authorities for party {} of type {}", partyId, partyType);
  }
}
