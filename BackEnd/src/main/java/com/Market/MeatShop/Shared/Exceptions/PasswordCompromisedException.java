package com.Market.MeatShop.Shared.Exceptions;

public class PasswordCompromisedException extends RuntimeException {
  public PasswordCompromisedException(String message) {
    super(message);
  }
}
