package com.Market.MeatShop.Parties.Utils;

import com.Market.MeatShop.Parties.DTOs.Requests.UpdatePartyReq;
import com.Market.MeatShop.Parties.Entities.Party;
import com.Market.MeatShop.Parties.Enums.PartyType;

import java.util.Objects;

public class PartyComparison {

    /**
     * Compares party fields that are present in the update request
     * Ignores timestamps (createdAt, updatedAt) and other auto-generated fields
     * 
     * @param original The original party before updates
     * @param updated The party after applying updates
     * @param updateRequest The request containing fields to check
     * @return true if no changes detected, false if changes exist
     */
    public static boolean hasNoChanges(Party original, Party updated, UpdatePartyReq updateRequest) {
        // Check partyName if present in request and not null
        if (updateRequest.partyName() != null) {
            if (!Objects.equals(original.getPartyName(), updated.getPartyName())) {
                return false; // Changes detected
            }
        }

        // Check partyAddress if present in request and not null
        if (updateRequest.partyAddress() != null) {
            if (!Objects.equals(original.getPartyAddress(), updated.getPartyAddress())) {
                return false; // Changes detected
            }
        }

        // Check partyType if present in request and not null
        if (updateRequest.partyType() != null) {
            if (!Objects.equals(original.getPartyType(), updated.getPartyType())) {
                return false; // Changes detected
            }
        }

        // If we get here, no changes were detected in the requested fields
        return true;
    }

    /**
     * Alternative method that checks if any changes exist (opposite of hasNoChanges)
     * 
     * @param original The original party before updates
     * @param updated The party after applying updates
     * @param updateRequest The request containing fields to check
     * @return true if changes detected, false if no changes
     */
    public static boolean hasChanges(Party original, Party updated, UpdatePartyReq updateRequest) {
        return !hasNoChanges(original, updated, updateRequest);
    }
}
