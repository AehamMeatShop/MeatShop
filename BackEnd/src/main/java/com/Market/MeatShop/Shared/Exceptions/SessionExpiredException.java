package com.Market.MeatShop.Shared.Exceptions;

public class SessionExpiredException extends RuntimeException {
  public SessionExpiredException(String message) {
    super(message);
  }
}
