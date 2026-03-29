package com.Market.MeatShop.Products.Mappers;

import com.Market.MeatShop.Products.DTOs.ProdCompViewDTO;
import com.Market.MeatShop.Products.Entities.ProductComponent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductComponentMapper {
    @Mapping(source = "componentProduct.productName", target = "productName")
    @Mapping(source = "componentProduct.description", target = "description")
    @Mapping(source = "componentProduct.productType", target = "productType")
    ProdCompViewDTO toViewDTO(ProductComponent productComponent);

    @Mapping(source = "componentProduct.productName", target = "productName")
    @Mapping(source = "componentProduct.description", target = "description")
    @Mapping(source = "componentProduct.productType", target = "productType")
    List<ProdCompViewDTO> toViewDTOList(List<ProductComponent> productComponents);


}
