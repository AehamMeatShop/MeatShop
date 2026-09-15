package com.Market.MeatShop.Shared.Exceptions;

import com.Market.MeatShop.Security.Assemblers.SecurityIdentity;
import lombok.Data;

public class SessionNotFoundException extends RuntimeException {

  public SessionNotFoundException(String message) {
    super(message);
  }
}
