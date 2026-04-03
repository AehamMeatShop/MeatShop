package com.Market.MeatShop.Products.DTOs.Requests;

import com.Market.MeatShop.Products.Enums.SellBehavior;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellProdStockReq extends BaseStockMoveReq{
    @NotNull(message = "product id is required")
    @Min(1)
    private Long productId;

    @NotNull
    @DecimalMin(value = "0.001")
    private BigDecimal quantity ;

    @NotNull(message = "component id is required")
    @Min(1)
    private Long componentId;


    @NotNull(message = "the type of behavior cannot be null")
    private SellBehavior behavior;
}
