package com.Market.MeatShop.Finances.Entities;
import com.Market.MeatShop.Finances.Enums.InvoiceType;
import com.Market.MeatShop.Parties.Entities.Party;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;


@Table(name= "invoices")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {

    @Id
     @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "invoices_seq_gen",
            sequenceName = "invoices_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column( name = "invoice_type" , nullable = false )
    @Enumerated(EnumType.STRING)
    InvoiceType invoiceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id" , nullable = false)
    private Party party;

    @Column(name = "notes" , nullable = true ,length = 1023)
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at" , nullable = false ,updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at" , nullable = true)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "invoice")
    private List<InvoiceComponent> invoicesComponents;

    @OneToMany(mappedBy = "invoice" , fetch = FetchType.LAZY)
    List<CashTransaction> cashTransactions;


}
