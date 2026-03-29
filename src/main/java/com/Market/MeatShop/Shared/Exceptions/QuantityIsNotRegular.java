package com.Market.MeatShop.Shared.Exceptions;

public class QuantityIsNotRegular extends RuntimeException {
    public QuantityIsNotRegular(String message) {
        super(message);
    }
}
