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
        @AssertTrue(message = "components are not in right way")
          private boolean isComponentsAreInRightWay(){
                BigDecimal totalratio = new BigDecimal("0");
                for(ComponentRatioDTO component : components){
                  if(component.componentProductID().equals(parentProductId)){
                    return false;
                  }
                  totalratio=totalratio.add(component.ratioInKg());

                }


            return totalratio.compareTo(BigDecimal.ONE) == 0;

        }
}
