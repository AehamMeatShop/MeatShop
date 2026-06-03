package com.Market.MeatShop.Employees.DTOs.Requests;

import com.Market.MeatShop.Employees.Enums.EmployeeStatus;
import jakarta.validation.constraints.DecimalMin;
import org.hibernate.validator.constraints.Length;

public record UpdateEmployeeProfileReq(
    @Length(min = 3, max = 100) String name,
    @Length(min = 3, max = 100) String address,
    @Length(min = 3, max = 100) String email,
    @Length(min = 8, max = 100) String password,
    @DecimalMin(value = "0") Long salary,
    EmployeeStatus status) {}
