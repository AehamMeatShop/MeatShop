package com.Market.MeatShop.Products.Repositories;

import com.Market.MeatShop.Products.Entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface ProductRepo extends JpaRepository<Product,Long> , JpaSpecificationExecutor<Product> {

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.components WHERE p.id = :id")
    Optional<Product> findByIdWithComponents(@Param("id") Long id);
    List<Product> findByCategoryId( long id);
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);
}
