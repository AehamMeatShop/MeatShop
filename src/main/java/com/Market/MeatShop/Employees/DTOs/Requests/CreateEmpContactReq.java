package com.Market.MeatShop.Employees.DTOs.Requests;

import jakarta.validation.constraints.NotNull;

public record CreateEmpContactReq(
        @NotNull(message = "party id cannot be null")
        Long employeeId,

        @NotNull(message = "method cannot be null")
        String method,

        @NotNull(message = "identifier cannot be null")
        String identifier
) {
}
