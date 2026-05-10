package com.Market.MeatShop.Finances.Entities;
import com.Market.MeatShop.Finances.Enums.CashTransactionType;
import com.Market.MeatShop.Parties.Entities.Party;
import jakarta.persistence.*;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;


@Table(name= "cash_transactions")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CashTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "cash_transactions_seq_gen",
            sequenceName = "cash_transactions_seq",
            allocationSize = 50
    )
    private Long id;

    @Column( name = "type",nullable = false)
    @Enumerated(EnumType.STRING )
    private CashTransactionType type;

    @ManyToOne(fetch = FetchType.LAZY )
    @JoinColumn(name = "party_id" , nullable = false)
    private Party party;

    @Column(name = "notes" , length = 1023)
    private String notes;
    
    @CreationTimestamp
    @Column(name = "created_at" , nullable = false ,updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "ammount" , nullable = false)
    @DecimalMin(value = "1" )
    private long amount;



    @AssertTrue(message = "the party most be exist if the trans type is not expenses")
    public boolean isValidParty() {
        if(type == CashTransactionType.EXPENSES ){
            return true;
        }
         return party != null;
    }

    @ManyToOne
    @JoinColumn(name = "invoice_id" , nullable = true)
    private Invoice invoice;
}
