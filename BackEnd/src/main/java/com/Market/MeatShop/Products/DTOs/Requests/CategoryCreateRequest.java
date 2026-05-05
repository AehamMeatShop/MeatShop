package com.Market.MeatShop.Products.DTOs.Requests;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record CategoryCreateRequest(
        @Length(min = 3, max = 255 , message = "length out of range between 3 , 255")
        @NotNull(message = "name of category cannot be null")
        String name
) {
}
