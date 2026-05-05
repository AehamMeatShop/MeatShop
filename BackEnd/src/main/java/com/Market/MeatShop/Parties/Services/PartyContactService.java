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
import org.springframework.stereotype.Service;

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

    return partyContactMapper.toViewDTO(partyContact);
    }
    public PartyContactViewDTO updatePartyContact(UpdatePartyContactReq req , Long id){
      PartyContact partyContact=findByIdEn(id);
      PartyContact originalCopy=partyContactMapper.clone(partyContact);
      partyContact = partyContactMapper.toEntity(req , partyContact);
      if(originalCopy.equals(partyContact)){
          throw new IllegalArgumentException("no changes");
      }
      partyContactRepo.save(partyContact);
      return partyContactMapper.toViewDTO(partyContact);
    }
    public void deletePartyContact(Long id){
         findByIdEn(id);
        partyContactRepo.deleteById(id);
    }
}
