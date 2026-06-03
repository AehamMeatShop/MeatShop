package com.Market.MeatShop.Security.Assemblers;

import com.Market.MeatShop.Security.Enums.SecuritySubjectType;

public interface SecuritySubjectProvider {

  SecuritySubjectType supports();

  SecurityIdentity getSubject(Long id);
}
