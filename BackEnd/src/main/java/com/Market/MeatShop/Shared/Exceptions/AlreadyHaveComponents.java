package com.Market.MeatShop.Shared.Exceptions;

public class AlreadyHaveComponents extends RuntimeException {
    public AlreadyHaveComponents(String message) {
        super(message);
    }
}
