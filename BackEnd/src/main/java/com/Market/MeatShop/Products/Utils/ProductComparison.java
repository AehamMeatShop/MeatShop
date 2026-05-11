package com.Market.MeatShop.Products.Utils;

import com.Market.MeatShop.Products.DTOs.Requests.ProductUpdateRequest;
import com.Market.MeatShop.Products.Entities.Product;
import com.Market.MeatShop.Products.Enums.ProductTypes;

import java.util.Objects;

public class ProductComparison {

    /**
     * Compares product fields that are present in the update request
     * Ignores timestamps (createdAt, updatedAt) and other auto-generated fields
     * 
     * @param original The original product before updates
     * @param updated The product after applying updates
     * @param updateRequest The request containing fields to check
     * @return true if no changes detected, false if changes exist
     */
    public static boolean hasNoChanges(Product original, Product updated, ProductUpdateRequest updateRequest) {
        // Check productName if present in request and not null
        if (updateRequest.productName() != null) {
            if (!Objects.equals(original.getProductName(), updated.getProductName())) {
                return false; // Changes detected
            }
        }

        // Check description if present in request and not null
        if (updateRequest.description() != null) {
            if (!Objects.equals(original.getDescription(), updated.getDescription())) {
                return false; // Changes detected
            }
        }

        // Check productType if present in request and not null
        if (updateRequest.productType() != null) {
            if (!Objects.equals(original.getProductType(), updated.getProductType())) {
                return false; // Changes detected
            }
        }

        // Check categoryId if present in request and not null
        if (updateRequest.categoryId() != null) {
            // Compare category IDs since we're dealing with entities
            Long originalCategoryId = original.getCategory() != null ? original.getCategory().getId() : null;
            Long updatedCategoryId = updated.getCategory() != null ? updated.getCategory().getId() : null;
            
            if (!Objects.equals(originalCategoryId, updatedCategoryId)) {
                return false; // Changes detected
            }
        }

        // If we get here, no changes were detected in the requested fields
        return true;
    }

    /**
     * Alternative method that checks if any changes exist (opposite of hasNoChanges)
     * 
     * @param original The original product before updates
     * @param updated The product after applying updates
     * @param updateRequest The request containing fields to check
     * @return true if changes detected, false if no changes
     */
    public static boolean hasChanges(Product original, Product updated, ProductUpdateRequest updateRequest) {
        return !hasNoChanges(original, updated, updateRequest);
    }
}
