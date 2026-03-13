package com.Market.MeatShop.Parties.Entities;
import com.Market.MeatShop.Finances.Entities.Invoice;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Table(name= "parties")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Party {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "party_name",nullable = false)
    private String partyName;

    @Column( name = "party_address",nullable = false)
    private String partyAddress;

    @OneToMany(mappedBy = "party" , fetch = FetchType.EAGER)
    List<PartyContact> partyContacts;

    @OneToMany(mappedBy = "party" , fetch = FetchType.LAZY)
    List<Invoice> invoices;

}
