package com.Market.MeatShop.Products.DTOs.Requests;

import com.Market.MeatShop.Products.Enums.ProductTypes;

import java.time.LocalDateTime;

public record ProductFilterRequest(
        Long categoryId ,

        String productName ,

        LocalDateTime createdAt ,

        LocalDateTime updatedAt ,

        String description ,

        ProductTypes productType
) {
}
