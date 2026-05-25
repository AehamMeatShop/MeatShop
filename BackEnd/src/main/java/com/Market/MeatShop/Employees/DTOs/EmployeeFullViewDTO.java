package com.Market.MeatShop.Employees.DTOs;

import com.Market.MeatShop.Parties.DTOs.PartyViewDTO;

public record EmployeeFullViewDTO(
        EmployeeViewDTO employeeInfo,
        PartyViewDTO partyInfo
) {
}
