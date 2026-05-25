package com.Market.MeatShop.Employees.DTOs.Requests;

import com.Market.MeatShop.Employees.Enums.EmployeeStatus;
import jakarta.validation.constraints.DecimalMin;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EmployeeFilterReq(
        @Length(min = 3, max = 100) String name,
        LocalDateTime fromCreatedAt,
        LocalDateTime toCreatedAt,
        LocalDateTime fromUpdatedAt,
        LocalDateTime toUpdatedAt,
        String address,
        String email,
        @DecimalMin(value = "0") BigDecimal minSalary,
        @DecimalMin(value = "0") BigDecimal maxSalary,
        Long roleId,
        EmployeeStatus status) {}
