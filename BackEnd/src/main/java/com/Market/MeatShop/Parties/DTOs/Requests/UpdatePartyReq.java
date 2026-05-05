package com.Market.MeatShop.Parties.DTOs.Requests;

import com.Market.MeatShop.Parties.Enums.PartyType;
import jakarta.validation.constraints.NotNull;

public record UpdatePartyReq(



        String partyName,

        String partyAddress,

        PartyType partyType
) {
}
