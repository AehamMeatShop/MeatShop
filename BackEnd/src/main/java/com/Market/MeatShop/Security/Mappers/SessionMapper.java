package com.Market.MeatShop.Security.Mappers;

import com.Market.MeatShop.Security.DTOs.SessionViewDto;
import com.Market.MeatShop.Security.Entities.Session;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SessionMapper {
  SessionViewDto toViewDto(Session session);
}
