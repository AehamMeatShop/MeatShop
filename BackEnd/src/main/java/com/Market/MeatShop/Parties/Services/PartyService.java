package com.Market.MeatShop.Parties.Services;

import com.Market.MeatShop.Parties.DTOs.PartyViewDTO;
import com.Market.MeatShop.Parties.DTOs.Requests.CreatePartyRequest;
import com.Market.MeatShop.Parties.DTOs.Requests.PartyFilterReq;
import com.Market.MeatShop.Parties.DTOs.Requests.UpdatePartyReq;
import com.Market.MeatShop.Parties.DTOs.Responses.UpdatePartyResp;
import com.Market.MeatShop.Parties.Entities.Party;
import com.Market.MeatShop.Parties.Mappers.PartyMapper;
import com.Market.MeatShop.Parties.QueryRoles.PartyQueryRoles;
import com.Market.MeatShop.Parties.Repositories.PartyRepo;
import com.Market.MeatShop.Parties.Spesifications.PartySpecifications;
import com.Market.MeatShop.Products.QueryRoles.ProductQueryRoles;
import com.Market.MeatShop.Shared.Exceptions.TargetNotFound;
import com.Market.MeatShop.Parties.Utils.PartyComparison;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PartyService {
  private final PartyRepo partyRepo;
  private PartyMapper partyMapper;

  public PartyService(PartyRepo partyRepo, PartyMapper partyMapper) {
    this.partyRepo = partyRepo;
    this.partyMapper = partyMapper;
  }

  public PartyViewDTO createParty(CreatePartyRequest req) {
    PartyViewDTO resp = partyMapper.toViewDTO(partyRepo.save(partyMapper.toEntity(req)));
    log.info("party created {}", resp);
    return resp;
  }

  public PartyViewDTO findPartyById(long id) {
    Optional<Party> party = partyRepo.findById(id);
    if (party.isEmpty()) {
      throw new TargetNotFound("party : " + id + " not found");
    }
    PartyViewDTO resp = partyMapper.toViewDTO(party.get());
    log.info("party returned {}", resp);
    return resp;
  }

  public Party findPartyByIdEn(long id) {
    Optional<Party> party = partyRepo.findById(id);
    if (party.isEmpty()) {
      throw new TargetNotFound("party : " + id + " not found");
    }
    log.info("party requested and returned as entity {}", party.get().getId());
    return party.get();
  }

  private Specification<Party> createSpecification(PartyFilterReq filter) {
    Specification<Party> spec = Specification.allOf();
    if (filter.partyName() != null) {
      spec = spec.and(PartySpecifications.likePartyName(filter.partyName()));
    }

    if (filter.partyAddress() != null) {
      spec = spec.and(PartySpecifications.likePartyAddress(filter.partyAddress()));
    }
    if (filter.fromCreatedAt() != null || filter.toCreatedAt() != null) {
      spec =
          spec.and(
              PartySpecifications.betweenCreatingDates(
                  filter.fromCreatedAt(), filter.toCreatedAt()));
    }
    if (filter.fromUpdatedAt() != null || filter.toUpdatedAt() != null) {
      spec =
          spec.and(
              PartySpecifications.betweenUpdatingDates(
                  filter.fromUpdatedAt(), filter.toUpdatedAt()));
    }
    return spec;
  }

  public Page<PartyViewDTO> findByFilter(PartyFilterReq filter, Pageable pageable) {
    pageable
        .getSort()
        .forEach(
            sort -> {
              if (!PartyQueryRoles.ALLOWED_SORT_FIELDS.contains(sort.getProperty())) {
                throw new IllegalArgumentException("Sorting not allowed on: " + sort.getProperty());
              }
            });
    if (pageable.getPageSize() > PartyQueryRoles.maxPageSize) {
      throw new IllegalArgumentException(
          "Page size is greater than " + ProductQueryRoles.maxPageSize);
    }
    Specification<Party> spec = createSpecification(filter);
    Page<Party> partyPage = partyRepo.findAll(spec, pageable);
    Page<PartyViewDTO> resp = partyPage.map(partyMapper::toViewDTO);
    log.info("parties returned {}", resp.getContent());
    return resp;
  }

  public List<PartyViewDTO> findByFilterServ(PartyFilterReq filter, List<Long> ids) {
    Specification<Party> spec = createSpecification(filter);
    spec = spec.and(PartySpecifications.inIds(ids));
    List<Party> parties = partyRepo.findAll(spec);

    return partyMapper.toViewDTO(parties);
  }

  @Transactional
  public UpdatePartyResp updateParty(UpdatePartyReq req, Long id) {
    Party party = findPartyByIdEn(id);
    Party orginalCopy = partyMapper.clone(party);
    partyMapper.fromUpdateReq(req, party);
    
    // Use manual comparison to check if any changes were made
    if (PartyComparison.hasNoChanges(orginalCopy, party, req)) {
      return new UpdatePartyResp(false, partyMapper.toViewDTO(party));
    }
    
    partyRepo.save(party);
    UpdatePartyResp resp = new UpdatePartyResp(true, partyMapper.toViewDTO(party));
    log.info("party updated {}", resp.partyInfo());
    return resp;
  }

  @Transactional
  public void deleteParty(Long id) {
    findPartyByIdEn(id);
    partyRepo.deleteById(id);
    log.info("party deleted {}", id);
  }
}
