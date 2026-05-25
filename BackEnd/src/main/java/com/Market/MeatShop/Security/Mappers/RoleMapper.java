package com.Market.MeatShop.Security.Mappers;

import com.Market.MeatShop.Security.DTOs.Requests.CreateRoleRequest;
import com.Market.MeatShop.Security.DTOs.Requests.UpdateRoleRequest;
import com.Market.MeatShop.Security.DTOs.RoleViewDto;
import com.Market.MeatShop.Security.Entities.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {
  RoleViewDto toViewDto(Role role);

  Role toEntity(CreateRoleRequest roleViewDto);

  void updateEntityFromDto(UpdateRoleRequest request, @MappingTarget Role role);
}
