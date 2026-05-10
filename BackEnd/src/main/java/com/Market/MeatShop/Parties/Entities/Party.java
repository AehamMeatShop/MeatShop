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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.LocalDateTime;
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
            sequenceName = "parties_seq",
            allocationSize = 50
    )
    private Long id;

    @Column(name = "party_name",nullable = false)
    private String partyName;

    @Column( name = "party_address",nullable = false , length = 1023)
    private String partyAddress;

    @OneToMany(mappedBy = "party" , fetch = FetchType.LAZY)
    List<PartyContact> partyContacts;


    @Column(name = "party_type" , nullable = false )
    @Enumerated(EnumType.STRING )
    PartyType  partyType;

    @CreationTimestamp
    @Column(name = "created_at" , nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at" , nullable = true)
    private LocalDateTime updatedAt;


}
