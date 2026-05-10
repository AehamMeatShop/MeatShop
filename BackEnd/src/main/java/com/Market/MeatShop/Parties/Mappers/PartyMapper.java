package com.Market.MeatShop.Parties.Mappers;

import com.Market.MeatShop.Parties.DTOs.PartyViewDTO;
import com.Market.MeatShop.Parties.DTOs.Requests.CreatePartyRequest;
import com.Market.MeatShop.Parties.DTOs.Requests.UpdatePartyReq;
import com.Market.MeatShop.Parties.Entities.Party;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PartyMapper {
  PartyViewDTO toViewDTO(Party party);

  Party toEntity(PartyViewDTO dto);

  List<PartyViewDTO> toViewDTO(List<Party> parties);

  Party toEntity(CreatePartyRequest request);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Party fromUpdateReq(UpdatePartyReq req, @MappingTarget Party party);

  @Mapping(target = "partyContacts", ignore = true)
  Party clone(Party party);
}
