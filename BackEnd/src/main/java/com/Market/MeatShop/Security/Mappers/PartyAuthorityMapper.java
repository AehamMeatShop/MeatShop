package com.Market.MeatShop.Security.Mappers;

import com.Market.MeatShop.Security.DTOs.AuthorityViewDto;
import com.Market.MeatShop.Security.DTOs.PartyAuthorityViewDto;
import com.Market.MeatShop.Security.Entities.PartyAuthority;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PartyAuthorityMapper {
  @Mapping(source = "authority.id", target = "authority.id")
  @Mapping(source = "authority.authority", target = "authority.authority")
  PartyAuthorityViewDto toViewDto(PartyAuthority partyAuthority);
}
