package com.Market.MeatShop.Products.DTOs.Requests;

import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransformProdStockReq extends BaseStockMoveReq {

    @NotNull
    private Map<Long, BigDecimal> inputs;

    @NotNull
    private Map<Long, BigDecimal> outputs;

    @AssertFalse
    public boolean isAnyEntryEmpty() {
        if(inputs==null || outputs==null){
            return true;
        }
        if(inputs.isEmpty() || outputs.isEmpty()){
            return true;
        }
        return false;

    }

    @AssertTrue(message = "there are product repeated in inputs and outputs")
    public boolean isProductsRepeated() {
        return Collections.disjoint(inputs.keySet(), outputs.keySet());
    }
    @AssertTrue(message = "all inputs must be positive")
    public boolean isAllInputsPositive() {

        if (inputs == null)
            return false;

        for (Map.Entry<Long, BigDecimal> entry : inputs.entrySet()) {

            if (entry.getKey() == null || entry.getValue() == null)
                return false;

            if (entry.getValue().compareTo(BigDecimal.ZERO) <= 0)
                return false;
        }

        return true;
    }

    @AssertTrue(message = "all outputs most be positive")
    public boolean isAllOutputsPositive() {
        if (outputs == null)
            return false;

        for (Map.Entry<Long, BigDecimal> entry : outputs.entrySet()) {

            if (entry.getKey() == null || entry.getValue() == null)
                return false;

            if (entry.getValue().compareTo(BigDecimal.ZERO) <= 0)
                return false;
        }
        return true;
    }

}
