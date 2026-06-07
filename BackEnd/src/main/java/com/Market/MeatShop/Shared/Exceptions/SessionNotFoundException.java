package com.Market.MeatShop.Shared.Exceptions;

import com.Market.MeatShop.Security.Assemblers.SecurityIdentity;
import lombok.Data;

public class SessionNotFoundException extends RuntimeException {
  private final SecurityIdentity identity;

  public SessionNotFoundException(String message, SecurityIdentity identity) {
    super(message);
    this.identity = identity;
  }

  public SecurityIdentity getIdentity() {
    return this.identity;
  }
}
