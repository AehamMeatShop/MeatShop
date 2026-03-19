package com.Market.MeatShop.Products.Repositories;

import com.Market.MeatShop.Products.Entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CategoryRepo extends JpaRepository<Category,Long> {

public Optional<Category> findByName(String name);
public boolean existsByName(String name);

}
