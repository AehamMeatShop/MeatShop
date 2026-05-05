package com.Market.MeatShop.Parties.DTOs.Requests;

import com.Market.MeatShop.Parties.Entities.PartyContact;
import com.Market.MeatShop.Parties.Enums.PartyType;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreatePartyRequest(

        @NotNull(message = "name cannot be null")
        String partyName,

        @NotNull(message = "party address cannot be null")
        String partyAddress,

        @NotNull(message = "party type cannot be null")
        PartyType partyType
) {
}
