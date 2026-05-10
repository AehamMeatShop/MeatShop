package com.Market.MeatShop.Products.Entities;

import com.Market.MeatShop.Finances.Entities.InvoiceComponent;
import com.Market.MeatShop.Products.Enums.ProductTypes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Table(name = "products")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "products_seq_gen",
            sequenceName = "products_seq",
            allocationSize = 50
    )
    private long id;

    @Column(name = "product_name" , nullable = false , length = 255 , unique = true)
    private String productName;

    @CreationTimestamp
    @Column(name = "created_at" , nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at" , nullable = true)
    private LocalDateTime updatedAt;

    @Column(name = "description"  , nullable = true , length = 1023)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(fetch = FetchType.LAZY , mappedBy = "parentProduct")
    private List<ProductComponent> components;

    @OneToMany(fetch = FetchType.LAZY , mappedBy = "componentProduct")
    private List<ProductComponent> includedInComposition;

    @OneToMany(fetch = FetchType.LAZY , mappedBy = "product")
    private List<StockMovment> stockMovements;


    @Column(name = "product_type" ,nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductTypes productType;
}
