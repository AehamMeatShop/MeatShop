package com.Market.MeatShop.Products.DTOs;

import com.Market.MeatShop.Products.Enums.ProductTypes;

import java.time.LocalDateTime;

public record ProductViewDTO(
          long id ,

          String productName ,

          LocalDateTime createdAt ,

          LocalDateTime updatedAt ,

          String description ,
          
          ProductTypes productType
) {
}
