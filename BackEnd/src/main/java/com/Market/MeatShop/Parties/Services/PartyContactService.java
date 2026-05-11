package com.Market.MeatShop.Parties.Services;

import com.Market.MeatShop.Parties.DTOs.PartyContactViewDTO;
import com.Market.MeatShop.Parties.DTOs.Requests.CreatePartyContactReq;
import com.Market.MeatShop.Parties.DTOs.Requests.UpdatePartyContactReq;
import com.Market.MeatShop.Parties.Entities.Party;
import com.Market.MeatShop.Parties.Entities.PartyContact;
import com.Market.MeatShop.Parties.Mappers.PartyContactMapper;
import com.Market.MeatShop.Parties.Repositories.PartyContactRepo;
import com.Market.MeatShop.Parties.Repositories.PartyRepo;
import com.Market.MeatShop.Shared.Exceptions.TargetNotFound;
import com.Market.MeatShop.Parties.Utils.PartyContactComparison;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PartyContactService {
    private final PartyRepo partyRepo;
    private final PartyContactRepo partyContactRepo;
    private final PartyService partyService;
    private PartyContactMapper partyContactMapper;
    public PartyContactService(PartyRepo partyRepo, PartyContactRepo partyContactRepo ,PartyService partyService
    ,PartyContactMapper partyContactMapper) {
        this.partyRepo = partyRepo;
        this.partyContactRepo = partyContactRepo;
        this.partyService = partyService;
        this.partyContactMapper = partyContactMapper;
    }
    public PartyContact findByIdEn(Long id){
        return partyContactRepo.findById(id).orElseThrow(() -> new TargetNotFound("Party Contact  : "+ id+ "  not found"));
    }
    public PartyContactViewDTO createPartyContact(CreatePartyContactReq req){
        Party party=partyService.findPartyByIdEn(req.partyId());
        PartyContact partyContact=partyContactMapper.toEntity(req);
        partyContact.setParty(party);

        partyContactRepo.save(partyContact);
        PartyContactViewDTO resp = partyContactMapper.toViewDTO(partyContact);
        log.info("party contact created {}", resp);
        return resp;
    }
    public PartyContactViewDTO updatePartyContact(UpdatePartyContactReq req , Long id){
      PartyContact partyContact=findByIdEn(id);
      PartyContact originalCopy=partyContactMapper.clone(partyContact);
      partyContact = partyContactMapper.toEntity(req , partyContact);
      
      // Use manual comparison to check if any changes were made
      if(PartyContactComparison.hasNoChanges(originalCopy, partyContact, req)){
          throw new IllegalArgumentException("no changes");
      }
      
      partyContactRepo.save(partyContact);
      PartyContactViewDTO resp = partyContactMapper.toViewDTO(partyContact);
      log.info("party contact updated {}", resp);
      return resp;
    }
    public void deletePartyContact(Long id){
         findByIdEn(id);
        partyContactRepo.deleteById(id);
        log.info("party contact deleted {}", id);
    }
}
