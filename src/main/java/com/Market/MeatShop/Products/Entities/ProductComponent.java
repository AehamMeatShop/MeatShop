package com.Market.MeatShop.Products.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.security.PrivateKey;

@Table(name= "product_components")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductComponent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product parentProduct;

    @Column(name = "ratio_in_kg",nullable = false )
    @DecimalMin(value = "0.001", message = "Value must be at least 0.01")
    @DecimalMax(value = "0.999", message = "Value must be at most 0.99")
    private double ratioInKg;

    @ManyToOne(fetch = FetchType.EAGER)
    private Product component;

}
