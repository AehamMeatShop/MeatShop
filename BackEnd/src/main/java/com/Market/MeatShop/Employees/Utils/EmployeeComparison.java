package com.Market.MeatShop.Employees.Utils;

import com.Market.MeatShop.Employees.DTOs.Requests.UpdateEmployeeReq;
import com.Market.MeatShop.Employees.Entities.Employee;
import com.Market.MeatShop.Employees.Enums.EmployeeRole;
import com.Market.MeatShop.Employees.Enums.EmployeeStatus;

import java.util.Objects;

public class EmployeeComparison {

    /**
     * Compares employee fields that are present in the update request
     * Ignores timestamps (createdAt, updatedAt) and other auto-generated fields
     * 
     * @param original The original employee before updates
     * @param updated The employee after applying updates
     * @param updateRequest The request containing fields to check
     * @return true if no changes detected, false if changes exist
     */
    public static boolean hasNoChanges(Employee original, Employee updated, UpdateEmployeeReq updateRequest) {
        // Check email if present in request and not null
        if (updateRequest.email() != null) {
            if (!Objects.equals(original.getEmail(), updated.getEmail())) {
                return false; // Changes detected
            }
        }

        // Check password if present in request and not null
        if (updateRequest.password() != null) {
            if (!Objects.equals(original.getPassword(), updated.getPassword())) {
                return false; // Changes detected
            }
        }

        // Check salary if present in request and not null
        if (updateRequest.salary() != null) {
            if (!Objects.equals(original.getSalary(), updated.getSalary())) {
                return false; // Changes detected
            }
        }

        // Check role if present in request and not null
        if (updateRequest.role() != null) {
            if (!Objects.equals(original.getRole(), updated.getRole())) {
                return false; // Changes detected
            }
        }

        // Check status if present in request and not null
        if (updateRequest.status() != null) {
            if (!Objects.equals(original.getStatus(), updated.getStatus())) {
                return false; // Changes detected
            }
        }

        // If we get here, no changes were detected in the requested fields
        return true;
    }

    /**
     * Alternative method that checks if any changes exist (opposite of hasNoChanges)
     * 
     * @param original The original employee before updates
     * @param updated The employee after applying updates
     * @param updateRequest The request containing fields to check
     * @return true if changes detected, false if no changes
     */
    public static boolean hasChanges(Employee original, Employee updated, UpdateEmployeeReq updateRequest) {
        return !hasNoChanges(original, updated, updateRequest);
    }
}
