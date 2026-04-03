package com.Market.MeatShop.Finances.Entities;


import jakarta.persistence.*;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;



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
            sequenceName = "invoice_components_id_seq",
            allocationSize = 1
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





}
