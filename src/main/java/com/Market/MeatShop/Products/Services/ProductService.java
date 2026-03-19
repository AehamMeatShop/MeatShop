package com.Market.MeatShop.Products.Services;

import com.Market.MeatShop.Products.DTOs.ProductViewDTO;
import com.Market.MeatShop.Products.DTOs.Requests.ProductCreateRequest;
import com.Market.MeatShop.Products.Entities.Category;
import com.Market.MeatShop.Products.Entities.Product;
import com.Market.MeatShop.Products.Mappers.CategoryMapper;
import com.Market.MeatShop.Products.Mappers.ProductMapper;
import com.Market.MeatShop.Products.Repositories.CategoryRepo;
import com.Market.MeatShop.Products.Repositories.ProductRepo;
import com.Market.MeatShop.Shared.Exceptions.TargetNotFound;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepo productRepo;
    private final ProductMapper productMapper;
    private  CategoryMapper categoryMapper;
    private final CategoryRepo categoryRepo;
    public ProductService( ProductRepo productRepo , ProductMapper productMapper ,
                           CategoryMapper categoryMapper , CategoryRepo categoryRepo ) {
        this.productMapper = productMapper;
        this.productRepo = productRepo;
        this.categoryMapper = categoryMapper;
        this.categoryRepo = categoryRepo;

    }

    public List<ProductViewDTO> findAll() {
        List<Product> products = productRepo.findAll();
        List<ProductViewDTO> productViewDTOList =productMapper.toProductViewDTOList(products);
        return productViewDTOList;
    }

    public ProductViewDTO createProduct(ProductCreateRequest productCreateRequest) {

      Product product =  productMapper.toProduct(productCreateRequest);

      Optional<Category> category = categoryRepo.findById(productCreateRequest.categoryId());

      if (!category.isPresent()) {
          throw new TargetNotFound("Category : " + productCreateRequest.categoryId()+" not found");
      }
      product.setCategory(category.get());
      product = productRepo.saveAndFlush(product);

        return productMapper.toProductViewDTO(product);




    }



}
