package com.Market.MeatShop.Finances.Entities;


import com.Market.MeatShop.Products.Entities.Product;
import com.Market.MeatShop.Products.Entities.StockMovment;
import jakarta.persistence.*;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Table(name= "invoice_components")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id" , nullable = false)
    private Product product;

    @Column(name = "quantity_kg" , nullable = false )
    @DecimalMin(value = "0.001" , message = "quantity at least 1 gram = 0.001 kg")
    private double quantityKg;

    @Column(name = "price_kg")
    private double priceKg;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @OneToMany(mappedBy = "invoice_component" , fetch = FetchType.LAZY)
    private List<StockMovment> stockMovments;



}
