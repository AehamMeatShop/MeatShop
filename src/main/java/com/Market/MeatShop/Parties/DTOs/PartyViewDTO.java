package com.Market.MeatShop.Parties.DTOs;

import com.Market.MeatShop.Parties.Entities.PartyContact;
import com.Market.MeatShop.Parties.Enums.PartyType;
import jakarta.persistence.*;
import lombok.Builder;

import java.util.List;


public record PartyViewDTO(

        Long id,


        String partyName,


         String partyAddress,


        List<PartyContactViewDTO> partyContacts,


        PartyType partyType
) {
}
