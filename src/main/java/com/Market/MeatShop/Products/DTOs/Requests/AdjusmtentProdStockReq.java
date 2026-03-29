package com.Market.MeatShop.Products.DTOs.Requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AdjusmtentProdStockReq(
        @NotNull(message = "product id is required")
        @Min(1)
        Long productId,

        @NotNull
        BigDecimal quantity ,

        String notes
) {
}
