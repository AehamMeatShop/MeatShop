package com.Market.MeatShop.Employees.DTOs.Rsponses;

import com.Market.MeatShop.Employees.DTOs.EmployeeViewDTO;

public record EmployeeUpdateResponse(EmployeeViewDTO oldData, EmployeeViewDTO newData) {}
