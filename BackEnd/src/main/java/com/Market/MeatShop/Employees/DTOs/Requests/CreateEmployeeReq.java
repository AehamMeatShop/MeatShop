package com.Market.MeatShop.Employees.DTOs.Requests;

import com.Market.MeatShop.Employees.Enums.EmployeeStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record CreateEmployeeReq(
    @NotNull @Length(min = 3, max = 100) String name,
    @NotNull @Length(min = 3, max = 100) String address,
    @NotNull @Length(min = 3, max = 100) String email,
    @NotNull @Length(min = 8, max = 100) String password,
    @NotNull @DecimalMin(value = "0") Long salary,
    @NotNull EmployeeStatus status) {}
