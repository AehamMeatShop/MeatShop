package com.Market.MeatShop.Security.Mappers;

import com.Market.MeatShop.Security.DTOs.PartyRoleViewDto;
import com.Market.MeatShop.Security.Entities.PartyRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface PartyRoleMapper {

  PartyRoleViewDto toViewDto(PartyRole partyRole);
}
