package com.Market.MeatShop.Parties.DTOs.Responses;

import com.Market.MeatShop.Parties.DTOs.PartyViewDTO;

public record UpdatePartyResp(
        boolean updated ,
        PartyViewDTO partyInfo
) {
}
