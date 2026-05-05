package com.Market.MeatShop.Products.DTOs.Requests;

import com.Market.MeatShop.Products.DTOs.ComponentRatioDTO;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record CreateProdCompsRequest(
        @NotNull
        Long parentProductId ,

        @NotEmpty(message = "components list cannot be empty ! ")
        @NotNull(message = "components list cannot be null !")

        List<ComponentRatioDTO> components

) {

}
