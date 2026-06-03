package com.Market.MeatShop.Security.QueryRoles;

import java.util.List;

public class SessionQueryRoles {
  public static List<String> ALLOWED_SORT_FIELDS =
      List.of("partyType", "partyId", "state", "createdAt", "updatedAt", "trustScore");

  public static int maxPageSize = 30;
  public static String defaultSortingField = "createdAt";
}
