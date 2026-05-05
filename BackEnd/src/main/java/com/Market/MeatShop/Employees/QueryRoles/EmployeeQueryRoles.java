package com.Market.MeatShop.Employees.QueryRoles;

import java.util.List;

public class EmployeeQueryRoles {
  public static List<String> ALLOWED_SORT_FIELDS =
      List.of(
          "email",
          "minSalary",
          "maxSalary",
          "role",
          "status",
          "name",
          " fromCreatedAt",
          "address",
          "fromUpdatedAt",
          " toCreatedAt ",
          "toUpdatedAt");

  public static int maxPageSize = 30;
  public static String defaultSortingField = "createdAt";
}
