package com.Market.MeatShop.Products.Specifications;

import com.Market.MeatShop.Products.Entities.Product;
import com.Market.MeatShop.Products.Enums.ProductTypes;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class ProductSpecfications {
    public static Specification<Product> likeProductName(String productName) {
     return  (root, query, builder) ->
             builder.like(root.get("productName").as(String.class), "%" + productName + "%");
    }

    public static Specification<Product> likeProductType(ProductTypes productType) {
        return  (root, query, builder) ->
                builder.like(root.get("productType"), "%"+productType+"%");
    }

    public static Specification<Product> likeDescription(String description) {

        return  (root, query, builder)  ->
                builder.like(root.get("description").as(String.class), "%" + description + "%");
    }

  public static Specification<Product> hasCreatedAt(LocalDateTime createdAt) {
        return    (root, query, builder) ->
                builder.equal(root.get("createdAt").as(LocalDateTime.class), createdAt);
  }

  public static Specification<Product> hasUpdatedAt(LocalDateTime updatedAt) {
      return    (root, query, builder) ->
              builder.equal(root.get("updatedAt").as(LocalDateTime.class), updatedAt);
  }

  public static  Specification<Product> hasCategoryId(Long categoryId) {
        return  (root, query, builder) ->
                builder.equal(root.get("category").get("id").as(Long.class), categoryId);
  }

}
