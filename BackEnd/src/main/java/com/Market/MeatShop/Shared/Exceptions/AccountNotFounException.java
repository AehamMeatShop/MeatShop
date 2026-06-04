package com.Market.MeatShop.Shared.Exceptions;

public class AccountNotFounException extends RuntimeException {
  public AccountNotFounException(String message) {
    super(message);
  }
}
