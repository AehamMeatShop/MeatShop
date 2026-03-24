package com.Market.MeatShop.Parties.Entities;
import com.Market.MeatShop.Employees.Entities.Employee;
import com.Market.MeatShop.Finances.Entities.CashTransaction;
import com.Market.MeatShop.Finances.Entities.Invoice;
import com.Market.MeatShop.Parties.Enums.PartyType;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;


@Table(name= "parties")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Party {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "parties_seq_gen",
            sequenceName = "parties_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "party_name",nullable = false)
    private String partyName;

    @Column( name = "party_address",nullable = false , length = 1023)
    private String partyAddress;

    @OneToMany(mappedBy = "party" , fetch = FetchType.EAGER)
    List<PartyContact> partyContacts;

    @OneToMany(mappedBy = "party" , fetch = FetchType.LAZY)
    List<Invoice> invoices;

    @OneToMany(mappedBy = "party")
    List<CashTransaction> cashTransactions;

    @Column(name = "party_type" , nullable = false )

    @Enumerated(EnumType.STRING )
    PartyType  partyType;

    @OneToOne(mappedBy = "party")
    private Employee employee;
}
