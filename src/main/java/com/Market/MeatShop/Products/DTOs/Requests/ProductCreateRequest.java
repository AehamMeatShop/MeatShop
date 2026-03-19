package com.Market.MeatShop.Products.DTOs.Requests;

import com.Market.MeatShop.Products.Enums.ProductTypes;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;



public record ProductCreateRequest(


        @NotNull( message = "name of category cannot be null")
        @Length(min = 3, max = 255 ,  message = "name is out of range 3 255 character")
        String productName ,

        @NotNull
        @DecimalMin(value = "1")
        Long categoryId ,

        @Length(max = 1023 , message = "length of description most be less than 1024 character")
        String description ,

        @Enumerated(EnumType.STRING)
        @NotNull(message = "the type of product cannot be null")
        ProductTypes productType
) {
}
