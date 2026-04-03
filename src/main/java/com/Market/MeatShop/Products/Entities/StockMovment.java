package com.Market.MeatShop.Products.Entities;

import com.Market.MeatShop.Finances.Entities.InvoiceComponent;
import com.Market.MeatShop.Products.Enums.StockMovementsTypes;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Table(name= "stock_movments")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockMovment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "stock_movments_seq_gen",
            sequenceName = "stock_movments_id_seq",
            allocationSize = 1
    )
    private long id;

    @ManyToOne
    @JoinColumn(name = "product_id" , nullable = false)
    private Product product;

    @Column( name = "created_at", nullable = false , updatable = false , insertable = false )
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column( name = "updated_at", nullable = true , insertable = false )
    @UpdateTimestamp
    LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "invoice_component_id", nullable = true)
    private InvoiceComponent invoiceComponent;

    @Column(name = "quantity" , nullable = false)
    private BigDecimal quantity;

    @Column(name = "stock_movment_type" , nullable = false)
    @Enumerated(EnumType.STRING)
    private StockMovementsTypes stockMovementsType;

    @Column(name = "notes" , nullable = true , length = 1023)
    private String notes;
}
