package com.Market.MeatShop.Parties.Utils;

import com.Market.MeatShop.Parties.DTOs.Requests.UpdatePartyContactReq;
import com.Market.MeatShop.Parties.Entities.PartyContact;

import java.util.Objects;

public class PartyContactComparison {

    /**
     * Compares party contact fields that are present in the update request
     * Ignores timestamps (createdAt, updatedAt) and other auto-generated fields
     * 
     * @param original The original party contact before updates
     * @param updated The party contact after applying updates
     * @param updateRequest The request containing fields to check
     * @return true if no changes detected, false if changes exist
     */
    public static boolean hasNoChanges(PartyContact original, PartyContact updated, UpdatePartyContactReq updateRequest) {
        // Check method if present in request and not null
        if (updateRequest.method() != null) {
            if (!Objects.equals(original.getMethod(), updated.getMethod())) {
                return false; // Changes detected
            }
        }

        // Check identifier if present in request and not null
        if (updateRequest.identifier() != null) {
            if (!Objects.equals(original.getIdentifier(), updated.getIdentifier())) {
                return false; // Changes detected
            }
        }

        // If we get here, no changes were detected in the requested fields
        return true;
    }

    /**
     * Alternative method that checks if any changes exist (opposite of hasNoChanges)
     * 
     * @param original The original party contact before updates
     * @param updated The party contact after applying updates
     * @param updateRequest The request containing fields to check
     * @return true if changes detected, false if no changes
     */
    public static boolean hasChanges(PartyContact original, PartyContact updated, UpdatePartyContactReq updateRequest) {
        return !hasNoChanges(original, updated, updateRequest);
    }
}
