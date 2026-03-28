package com.Market.MeatShop.Products.Services;

import com.Market.MeatShop.Products.DTOs.ProductViewDTO;
import com.Market.MeatShop.Products.DTOs.Requests.ProductCreateRequest;
import com.Market.MeatShop.Products.DTOs.Requests.ProductFilterRequest;

import com.Market.MeatShop.Products.DTOs.Requests.ProductUpdateRequest;
import com.Market.MeatShop.Products.Entities.Category;
import com.Market.MeatShop.Products.Entities.Product;
import com.Market.MeatShop.Products.Mappers.CategoryMapper;
import com.Market.MeatShop.Products.Mappers.ProductMapper;
import com.Market.MeatShop.Products.Repositories.CategoryRepo;
import com.Market.MeatShop.Products.Repositories.ProductRepo;
import com.Market.MeatShop.Products.Specifications.ProductSpecfications;
import com.Market.MeatShop.Shared.Exceptions.TargetNotFound;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public List<ProductViewDTO> findAllbyCategoryId(ProductFilterRequest filter) {
        Specification<Product> spec = Specification.allOf();
          if(filter.productName()!=null){
            spec=spec.and(ProductSpecfications.likeProductName(filter.productName()));
          }
          if(filter.productType()!=null){
              spec=spec.and(ProductSpecfications.likeProductType(filter.productType()));

          }
          if(filter.description()!=null){
              spec=spec.and(ProductSpecfications.likeDescription(filter.description()));
          }
          if(filter.createdAt()!=null){
              spec=spec.and(ProductSpecfications.hasCreatedAt(filter.createdAt()));
          }
          if(filter.updatedAt()!=null){
              spec=spec.and(ProductSpecfications.hasUpdatedAt(filter.updatedAt()));
          }
          if(filter.categoryId()!=null){

              spec=spec.and(ProductSpecfications.hasCategoryId(filter.categoryId()));
          }

          List<Product> products=productRepo.findAll(spec);

          return productMapper.toProductViewDTOList(products);
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


    public ProductViewDTO updateProduct(ProductUpdateRequest productUpdateRequest , long id) {
       Product product = productRepo.findById(id)
               .orElseThrow(() -> new TargetNotFound("Product Id : " + id));

       productMapper.updateFromRequest( productUpdateRequest,product);

       if(productUpdateRequest.categoryId() != null ) {

           Category category = categoryRepo.findById(productUpdateRequest.categoryId())
                   .orElseThrow(() -> new TargetNotFound("Category : " + productUpdateRequest.categoryId() + " not found"));


           product.setCategory(category);
       }

        product = productRepo.saveAndFlush(product);

        return productMapper.toProductViewDTO(product);

    }

    public void deleteProduct(long id) {
        if(!productRepo.existsById(id)){
            throw new TargetNotFound("Product Id : " + id);
        }
        productRepo.deleteById(id);

    }



}
