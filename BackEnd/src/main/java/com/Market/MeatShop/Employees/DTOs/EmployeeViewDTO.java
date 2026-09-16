package com.Market.MeatShop.Employees.DTOs;

import com.Market.MeatShop.Employees.Enums.EmployeeStatus;
import com.Market.MeatShop.Parties.DTOs.PartyViewDTO;

public record EmployeeViewDTO(
    Long id, String email, Long partyId, long salary, EmployeeStatus status) {}
