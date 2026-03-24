package com.Market.MeatShop.Products.Repositories;

import com.Market.MeatShop.Products.Entities.ProductComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ProductComponentRepo extends JpaRepository<ProductComponent,Long> {
}
