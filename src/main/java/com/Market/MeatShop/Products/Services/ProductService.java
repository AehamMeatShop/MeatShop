package com.Market.MeatShop.Products.Services;

import com.Market.MeatShop.Products.Mappers.ProductMapper;
import com.Market.MeatShop.Products.Repositories.ProductRepo;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private ProductRepo productRepo;
   private ProductMapper productMapper;
    public ProductService( ProductRepo productRepo , ProductMapper productMapper) {
        this.productMapper = productMapper;
        this.productRepo = productRepo;

    }



}
