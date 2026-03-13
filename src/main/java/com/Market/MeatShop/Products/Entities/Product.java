package com.Market.MeatShop.Products.Entities;

import com.Market.MeatShop.Finances.Entities.InvoiceComponent;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(fetch = FetchType.LAZY , mappedBy = "product")
    private List<ProductComponent> compontents;

    @OneToMany(fetch = FetchType.LAZY , mappedBy = "product")
    List<StockMovment>  stockMovments;

    @OneToMany(mappedBy = "product")
    List<InvoiceComponent> invoicesComponents;
}
