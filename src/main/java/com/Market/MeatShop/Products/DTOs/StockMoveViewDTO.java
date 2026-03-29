package com.Market.MeatShop.Products.DTOs;

import com.Market.MeatShop.Finances.Entities.InvoiceComponent;
import com.Market.MeatShop.Products.Entities.Product;
import com.Market.MeatShop.Products.Enums.StockMovmentsTypes;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StockMoveViewDTO(

    long id,

     String productName,

     LocalDateTime createdAt,

     LocalDateTime updatedAt,

     String productDescription,

     BigDecimal quantity,

     StockMovmentsTypes stockMovmentsType,

     String notes

) {
}
