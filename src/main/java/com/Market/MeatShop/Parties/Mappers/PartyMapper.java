package com.Market.MeatShop.Parties.Mappers;


import com.Market.MeatShop.Parties.DTOs.PartyViewDTO;
import com.Market.MeatShop.Parties.DTOs.Requests.CreatePartyRequest;
import com.Market.MeatShop.Parties.DTOs.Requests.UpdatePartyReq;
import com.Market.MeatShop.Parties.Entities.Party;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

import java.lang.annotation.Target;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PartyMapper {
    PartyViewDTO toViewDTO(Party party);
    Party toEntity(PartyViewDTO dto);
    List<PartyViewDTO> toViewDTO(List<Party> parties);
    Party toEntity(CreatePartyRequest request);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Party fromUpdateReq(UpdatePartyReq req, @MappingTarget Party party);
    Party clone(Party party);

}
