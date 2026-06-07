package com.Market.MeatShop.Shared.Exceptions;

import com.Market.MeatShop.Security.Assemblers.SecurityIdentity;

public class SessionStolenException extends RuntimeException {
  private final SecurityIdentity identity;

  public SessionStolenException(String message, SecurityIdentity identity) {
    super(message);
    this.identity = identity;
  }

  public SecurityIdentity getIdentity() {
    return this.identity;
  }
}
