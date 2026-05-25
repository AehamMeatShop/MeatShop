package com.Market.MeatShop.Employees.DTOs;

import com.Market.MeatShop.Employees.Enums.EmployeeStatus;
import com.Market.MeatShop.Parties.DTOs.PartyViewDTO;

public record EmployeeViewDTO(
          String email,

          long salary,

          Long roleId,

          EmployeeStatus status



) {
}
