package com.Market.MeatShop.Products.DTOs.Requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdjProdStockReq extends BaseStockMoveReq {
    @NotNull(message = "product id is required")
    @Min(1)
    private Long productId ;

    @NotNull
    private BigDecimal quantity ;



}
