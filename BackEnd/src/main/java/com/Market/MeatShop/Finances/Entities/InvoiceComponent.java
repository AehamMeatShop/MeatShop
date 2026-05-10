package com.Market.MeatShop.Finances.Entities;


import jakarta.persistence.*;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Table(name= "invoice_components")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "invoice_components_seq_gen",
            sequenceName = "invoice_components_seq",
            allocationSize = 50
    )
    private Long id;


    @Column(name = "product_id" , nullable = false)
    private Long productId;

    @Column(name = "quantity_kg" , nullable = false )
    @DecimalMin(value = "0.001" , message = "quantity at least 1 gram = 0.001 kg")
    private BigDecimal quantityKg;

    @Column(name = "price_kg")
    private BigDecimal priceKg;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;


    @CreationTimestamp
    @Column(name = "created_at" , nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at" , nullable = true)
    private LocalDateTime updatedAt;


}
