package com.Market.MeatShop.Products.DTOs.Requests;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
@JsonSubTypes({
        @JsonSubTypes.Type(value = AdjProdStockReq.class, name = "ADJUSTMENT"),
        @JsonSubTypes.Type(value = WastProdStockReq.class, name = "WAST")
})
public abstract class BaseStockMoveReq {


   protected String type ;
   protected String notes ;
}
