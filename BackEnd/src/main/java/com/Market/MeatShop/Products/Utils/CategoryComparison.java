package com.Market.MeatShop.Products.Utils;

import com.Market.MeatShop.Products.DTOs.Requests.CategoryCreateRequest;
import com.Market.MeatShop.Products.Entities.Category;

import java.util.Objects;

public class CategoryComparison {

    /**
     * Compares category fields that are present in the update request
     * Ignores timestamps (createdAt, updatedAt) and other auto-generated fields
     * 
     * @param original The original category before updates
     * @param updated The category after applying updates
     * @param updateRequest The request containing fields to check
     * @return true if no changes detected, false if changes exist
     */
    public static boolean hasNoChanges(Category original, Category updated, CategoryCreateRequest updateRequest) {
        // Check name if present in request and not null
        if (updateRequest.name() != null) {
            if (!Objects.equals(original.getName(), updated.getName())) {
                return false; // Changes detected
            }
        }

        // If we get here, no changes were detected in the requested fields
        return true;
    }

    /**
     * Alternative method that checks if any changes exist (opposite of hasNoChanges)
     * 
     * @param original The original category before updates
     * @param updated The category after applying updates
     * @param updateRequest The request containing fields to check
     * @return true if changes detected, false if no changes
     */
    public static boolean hasChanges(Category original, Category updated, CategoryCreateRequest updateRequest) {
        return !hasNoChanges(original, updated, updateRequest);
    }
}
