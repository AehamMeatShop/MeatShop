package com.Market.MeatShop.Products.Repositories;

import com.Market.MeatShop.Products.Entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product,Long> {
}
