package com.Market.MeatShop.Products.Mappers;

import com.Market.MeatShop.Products.DTOs.ProductViewDTO;
import com.Market.MeatShop.Products.DTOs.Requests.ProductCreateRequest;
import com.Market.MeatShop.Products.DTOs.Requests.ProductUpdateRequest;
import com.Market.MeatShop.Products.Entities.Product;

import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
  ProductViewDTO toProductViewDTO(Product product);

  Product toProduct(ProductViewDTO productViewDTO);

  List<ProductViewDTO> toProductViewDTOList(List<Product> products);

  List<Product> toProductList(List<ProductViewDTO> productViewDTOs);

  Product toProduct(ProductCreateRequest productCreateRequest);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateFromRequest(ProductUpdateRequest productUpdateRequest, @MappingTarget Product product);

  @Mapping(target = "category", ignore = true)
  @Mapping(target = "components", ignore = true)
  @Mapping(target = "includedInComposition", ignore = true)
  @Mapping(target = "stockMovements", ignore = true)
  Product clone(Product product);
}
