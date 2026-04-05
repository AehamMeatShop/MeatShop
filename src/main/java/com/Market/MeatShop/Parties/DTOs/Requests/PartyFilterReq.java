package com.Market.MeatShop.Parties.DTOs.Requests;

import com.Market.MeatShop.Parties.Enums.PartyType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record PartyFilterReq(
        String partyName,
        String partyAddress,
        PartyType partyType ,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime fromCreatedAt,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime fromUpdatedAt,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime toCreatedAt,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime toUpdatedAt
) {
}
