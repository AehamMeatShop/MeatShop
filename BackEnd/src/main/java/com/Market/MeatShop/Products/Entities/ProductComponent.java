package com.Market.MeatShop.Products.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.time.LocalDateTime;

@Table(name= "product_components")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductComponent {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "product_components_seq_gen",
            sequenceName = "product_components_seq",
            allocationSize = 50
    )
    private long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product parentProduct;

    @Column(name = "ratio_in_kg",nullable = false )
    @DecimalMin(value = "0.001", message = "Value must be at least 0.01")
    @DecimalMax(value = "0.999", message = "Value must be at most 0.99")
    private BigDecimal ratioInKg;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "component_id" , nullable = false)
    private Product componentProduct;

    @CreationTimestamp
    @Column(name = "created_at" , nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at" , nullable = true)
    private LocalDateTime updatedAt;


}
