package com.Market.MeatShop.Parties.Mappers;

import com.Market.MeatShop.Parties.DTOs.PartyContactViewDTO;
import com.Market.MeatShop.Parties.DTOs.PartyViewDTO;
import com.Market.MeatShop.Parties.Entities.Party;
import com.Market.MeatShop.Parties.Entities.PartyContact;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PartyContactMapper {
    PartyContact toEntity(PartyContactViewDTO dto);
    PartyContactViewDTO toViewDTO(PartyContact entity);
    List<PartyContactViewDTO> toViewDTOList(List<PartyContact> partContacts);


}
