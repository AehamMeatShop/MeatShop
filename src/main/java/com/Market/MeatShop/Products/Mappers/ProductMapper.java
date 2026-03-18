package com.Market.MeatShop.Products.Mappers;


import com.Market.MeatShop.Products.DTOs.ProductViewDTO;
import com.Market.MeatShop.Products.Entities.Product;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductViewDTO toProductViewDTO(Product product);
    Product toProduct(ProductViewDTO productViewDTO);
    List<ProductViewDTO> toProductViewDTOList(List<Product> products);
    List<Product> toProductList(List<ProductViewDTO> productViewDTOs);
}
