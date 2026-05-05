package com.Market.MeatShop.Products.DTOs;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ComponentRatioDTO(





      @DecimalMin(value = "0.001", message = "Value must be at least 0.001")
      @DecimalMax(value = "0.999", message = "Value must be at most 0.999")
      BigDecimal ratioInKg,

      @NotNull
      @Min(1)
      Long componentProductID


) {
}
