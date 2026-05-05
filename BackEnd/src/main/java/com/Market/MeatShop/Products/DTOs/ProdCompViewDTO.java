package com.Market.MeatShop.Products.DTOs;

import com.Market.MeatShop.Products.Enums.ProductTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProdCompViewDTO(
        long id ,

        String productName ,



        String description ,

        ProductTypes productType ,

        BigDecimal ratioInKg
) {
}
