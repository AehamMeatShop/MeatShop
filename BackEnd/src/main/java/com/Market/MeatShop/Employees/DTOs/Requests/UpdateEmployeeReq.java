package com.Market.MeatShop.Employees.DTOs.Requests;

import com.Market.MeatShop.Employees.Enums.EmployeeStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record UpdateEmployeeReq(


        @Length(min = 3, max = 100)
        String email,

        @Length(min = 8, max = 100)
        String password ,

        @DecimalMin(value = "0")
        Long salary,

        Long roleId,

        EmployeeStatus status
) {
}
