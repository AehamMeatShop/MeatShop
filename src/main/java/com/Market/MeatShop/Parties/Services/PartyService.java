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
import jakarta.transaction.Transactional;
import org.springframework.boot.data.autoconfigure.web.DataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class PartyService {
   private final PartyRepo partyRepo;
   private PartyMapper partyMapper;
   public PartyService(PartyRepo partyRepo , PartyMapper partyMapper) {
        this.partyRepo = partyRepo;
        this.partyMapper = partyMapper;
   }

   public PartyViewDTO createParty(CreatePartyRequest req) {

       return partyMapper.toViewDTO(partyRepo.save(partyMapper.toEntity(req)));

   }

   public Long createKnownParty(CreatePartyRequest req){
     return  partyRepo.save(partyMapper.toEntity(req)).getId();
   }
    public PartyViewDTO findPartyById(long id) {

        Optional<Party> party =  partyRepo.findById(id);
        if(party.isEmpty()){
            throw new TargetNotFound("party : "+id+" not found");
        }
        return partyMapper.toViewDTO(party.get());
    }
    public Party findPartyByIdEn(long id) {
        Optional<Party> party =  partyRepo.findById(id);
        if(party.isEmpty()){
            throw new TargetNotFound("party : "+id+" not found");
        }
        return party.get();

    }
   public Page<PartyViewDTO> findByFilter(PartyFilterReq filter, Pageable pageable) {
       pageable.getSort().forEach(sort -> {
           if(!PartyQueryRoles.ALLOWED_SORT_FIELDS.contains(sort.getProperty())){
               throw new IllegalArgumentException(
                       "Sorting not allowed on: " + sort.getProperty()
               );
           }
       });
       if(pageable.getPageSize() > PartyQueryRoles.maxPageSize){
           throw new IllegalArgumentException("Page size is greater than " + ProductQueryRoles.maxPageSize);
       }
     Specification<Party> spec = Specification.allOf();
       if(filter.partyName()!=null){
          spec= spec.and(PartySpecifications.likePartyName(filter.partyName()));

       }
       if(filter.partyAddress()!=null){
          spec= spec.and(PartySpecifications.likePartyAddress(filter.partyAddress()));
       }
       if(filter.fromCreatedAt()!=null || filter.toCreatedAt()!=null){
        spec = spec.and(PartySpecifications.betweenCreatingDates(filter.fromCreatedAt(),filter.toCreatedAt()));
       }
       if(filter.fromUpdatedAt()!=null || filter.toUpdatedAt()!=null){
           spec= spec.and(PartySpecifications.betweenUpdatingDates(filter.fromUpdatedAt(),filter.toUpdatedAt()));
       }


       Page<Party> partyPage = partyRepo.findAll(spec, pageable);

       return partyPage.map(partyMapper::toViewDTO);

   }
   @Transactional
   public UpdatePartyResp updateParty(UpdatePartyReq req, Long id){

       Party party=findPartyByIdEn(id);
      Party orginalCopy=partyMapper.clone(party);
      party=partyMapper.fromUpdateReq(req , party);
      if(orginalCopy.equals(party)){
          return new UpdatePartyResp(false , partyMapper.toViewDTO(party));

      }
      partyRepo.save(party);

       return new UpdatePartyResp(true , partyMapper.toViewDTO(party));
   }
   public void deleteParty(Long id){
       findPartyByIdEn(id);
       partyRepo.deleteById(id);
   }
}
