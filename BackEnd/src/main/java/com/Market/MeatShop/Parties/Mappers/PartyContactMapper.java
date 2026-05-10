package com.Market.MeatShop.Parties.Mappers;

import com.Market.MeatShop.Parties.DTOs.PartyContactViewDTO;
import com.Market.MeatShop.Parties.DTOs.PartyViewDTO;
import com.Market.MeatShop.Parties.DTOs.Requests.CreatePartyContactReq;
import com.Market.MeatShop.Parties.DTOs.Requests.UpdatePartyContactReq;
import com.Market.MeatShop.Parties.Entities.Party;
import com.Market.MeatShop.Parties.Entities.PartyContact;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PartyContactMapper {
  PartyContact toEntity(PartyContactViewDTO dto);

  PartyContactViewDTO toViewDTO(PartyContact entity);

  List<PartyContactViewDTO> toViewDTOList(List<PartyContact> partContacts);

  PartyContact toEntity(CreatePartyContactReq req);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  PartyContact toEntity(UpdatePartyContactReq req, @MappingTarget PartyContact partyContact);

  @Mapping(target = "party", ignore = true)
  PartyContact clone(PartyContact partyContact);
}
