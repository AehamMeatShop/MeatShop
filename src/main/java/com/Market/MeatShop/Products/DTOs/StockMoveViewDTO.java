package com.Market.MeatShop.Products.DTOs;

import com.Market.MeatShop.Products.Enums.StockMovementsTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StockMoveViewDTO(

    long id,

     String productName,

     LocalDateTime createdAt,

     LocalDateTime updatedAt,

     String productDescription,

     BigDecimal quantity,

     StockMovementsTypes stockMovementsType,

     String notes

) {
}
