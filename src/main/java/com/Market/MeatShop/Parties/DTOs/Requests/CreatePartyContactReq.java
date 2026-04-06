package com.Market.MeatShop.Parties.DTOs.Requests;

import jakarta.validation.constraints.NotNull;

public record CreatePartyContactReq(

        @NotNull(message = "party id cannot be null")
        Long partyId,

        @NotNull(message = "method cannot be null")
        String method,

        @NotNull(message = "identifier cannot be null")
        String identifier
) {
}
