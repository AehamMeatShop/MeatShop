package com.Market.MeatShop.Products.Services;


import com.Market.MeatShop.Products.DTOs.CategoryViewDTO;
import com.Market.MeatShop.Products.DTOs.Requests.CategoryCreateRequest;
import com.Market.MeatShop.Products.Entities.Category;
import com.Market.MeatShop.Products.Mappers.CategoryMapper;
import com.Market.MeatShop.Products.Repositories.CategoryRepo;
import com.Market.MeatShop.Products.Repositories.ProductRepo;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private CategoryRepo categoryRepo;
    private ProductRepo productRepo;
    private CategoryMapper categoryMapper;
    public ProductService(CategoryRepo categoryRepo , ProductRepo productRepo , CategoryMapper categoryMapper) {
        this.categoryRepo = categoryRepo;
        this.productRepo = productRepo;
        this.categoryMapper = categoryMapper;
    }



}
