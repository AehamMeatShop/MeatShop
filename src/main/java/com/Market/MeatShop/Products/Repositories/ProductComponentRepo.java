package com.Market.MeatShop.Products.Repositories;

import com.Market.MeatShop.Products.Entities.ProductComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductComponentRepo extends JpaRepository<ProductComponent,Long> {
    void deleteByParentProductId(long productId);

    List<ProductComponent> findByParentProductId(long productId);

}
