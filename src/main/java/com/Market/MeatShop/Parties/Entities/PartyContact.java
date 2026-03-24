package com.Market.MeatShop.Parties.Entities;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Table(name= "party_contacts"  ,uniqueConstraints = @UniqueConstraint(columnNames = {"method", "identifier"}))
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor


public class PartyContact {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "party_contacts_seq_gen",
            sequenceName = "party_contacts_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "method" , nullable = false)
    private String method;

    @Column(name = "identifier" , nullable = false)
    private String identifier;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "party_id" , nullable = false)
    private Party party;

}
