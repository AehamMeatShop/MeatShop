package com.Market.MeatShop.Products.DTOs.Requests;

import com.Market.MeatShop.Products.Enums.StockMovementsTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type" ,
        visible = true
)
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = AdjProdStockReq.class, name= "ADJUSTMENT"),
        @JsonSubTypes.Type(value = WastShrinkageProdStockReq.class, names = {"WASTE" , "SHRINKAGE"}),
        @JsonSubTypes.Type(value = SellProdStockReq.class, name = "SELL"),
        @JsonSubTypes.Type(value = PurchaseProdStockReq.class, name = "PURCHASE"),
        @JsonSubTypes.Type(value = TransformProdStockReq.class, name = "TRANSFORM")
})
public abstract class BaseStockMoveReq {

   @NotNull
   protected StockMovementsTypes type ;
   protected String notes ;
}
