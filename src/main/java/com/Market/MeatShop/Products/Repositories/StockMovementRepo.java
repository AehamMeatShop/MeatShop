package com.Market.MeatShop.Products.Repositories;

import com.Market.MeatShop.Products.Entities.StockMovment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface StockMovementRepo extends JpaRepository<StockMovment ,Long> , JpaSpecificationExecutor<StockMovment> {

    @Query(" SELECT COALESCE(SUM(s.quantity),0) FROM StockMovment s WHERE s.product.id = :productId ")
    BigDecimal getCurrentStock(@Param("productId") Long productId);
}
