package com.Market.MeatShop.Products.Mappers;

import com.Market.MeatShop.Products.DTOs.StockMoveViewDTO;
import com.Market.MeatShop.Products.Entities.StockMovment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface StockMovementMapper {
    @Mapping(source = "product.productName", target = "productName")
    @Mapping(source = "product.description" , target = "productDescription")
    StockMoveViewDTO toViewDto(StockMovment stockMovment);
}
