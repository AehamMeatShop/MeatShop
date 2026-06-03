package com.Market.MeatShop.Security.Mappers;

import com.Market.MeatShop.Security.DTOs.AuthorityViewDto;
import com.Market.MeatShop.Security.Entities.Authority;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthorityMapper {
  AuthorityViewDto toViewDto(Authority authority);

  List<AuthorityViewDto> toListViewDto(List<Authority> authorities);
}
