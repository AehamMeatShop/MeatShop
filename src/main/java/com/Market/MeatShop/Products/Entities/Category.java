package com.Market.MeatShop.Products.Entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Table(name = "categories")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(  name = "name",nullable = false , length = 255 , unique = true)
    private String name;

    @CreationTimestamp
    @Column(name = "created_at" , nullable = false ,unique = true)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at" , nullable = true)
    private  LocalDateTime updatedAt;

    @OneToMany(fetch = FetchType.LAZY , mappedBy = "category")
    private List<Product> products;


}
