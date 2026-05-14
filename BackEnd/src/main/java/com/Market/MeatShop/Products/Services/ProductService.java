package com.Market.MeatShop.Products.Services;

import com.Market.MeatShop.Products.DTOs.ProductViewDTO;
import com.Market.MeatShop.Products.DTOs.Requests.ProductCreateRequest;
import com.Market.MeatShop.Products.DTOs.Requests.ProductFilterRequest;

import com.Market.MeatShop.Products.DTOs.Requests.ProductUpdateRequest;
import com.Market.MeatShop.Products.Entities.Category;
import com.Market.MeatShop.Products.Entities.Product;

import com.Market.MeatShop.Products.Mappers.ProductMapper;
import com.Market.MeatShop.Products.QueryRoles.ProductQueryRoles;
import com.Market.MeatShop.Products.Repositories.CategoryRepo;
import com.Market.MeatShop.Products.Repositories.ProductRepo;
import com.Market.MeatShop.Products.Specifications.ProductSpecfications;
import com.Market.MeatShop.Products.Utils.ProductComparison;
import com.Market.MeatShop.Shared.Exceptions.TargetNotFound;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProductService {

  private final ProductRepo productRepo;
  private final ProductMapper productMapper;

  private final CategoryRepo categoryRepo;

  public ProductService(
      ProductRepo productRepo, ProductMapper productMapper, CategoryRepo categoryRepo) {

    this.productMapper = productMapper;
    this.productRepo = productRepo;

    this.categoryRepo = categoryRepo;
  }

  public List<ProductViewDTO> findAllProducts() {
    List<Product> products = productRepo.findAll();
    List<ProductViewDTO> productViewDTOList = productMapper.toProductViewDTOList(products);
    return productViewDTOList;
  }

  public Page<ProductViewDTO> findAllbyFilter(ProductFilterRequest filter, Pageable pageable) {

    pageable
        .getSort()
        .forEach(
            sort -> {
              if (!ProductQueryRoles.ALLOWED_SORT_FIELDS.contains(sort.getProperty())) {
                throw new IllegalArgumentException("Sorting not allowed on: " + sort.getProperty());
              }
            });
    if (pageable.getPageSize() > ProductQueryRoles.maxPageSize) {
      throw new IllegalArgumentException(
          "Page size is greater than " + ProductQueryRoles.maxPageSize);
    }
    Specification<Product> spec = Specification.allOf();
    if (filter.productName() != null) {
      spec = spec.and(ProductSpecfications.likeProductName(filter.productName()));
    }
    if (filter.productType() != null) {
      spec = spec.and(ProductSpecfications.likeProductType(filter.productType()));
    }
    if (filter.description() != null) {
      spec = spec.and(ProductSpecfications.likeDescription(filter.description()));
    }
    if (filter.createdAt() != null) {
      spec = spec.and(ProductSpecfications.hasCreatedAt(filter.createdAt()));
    }
    if (filter.updatedAt() != null) {
      spec = spec.and(ProductSpecfications.hasUpdatedAt(filter.updatedAt()));
    }
    if (filter.categoryId() != null) {

      spec = spec.and(ProductSpecfications.hasCategoryId(filter.categoryId()));
    }

    Page<Product> products = productRepo.findAll(spec, pageable);
    Page<ProductViewDTO> resp = products.map(productMapper::toProductViewDTO);
    log.info(" products returned {}", resp.getContent());
    return resp;
  }

  public ProductViewDTO createProduct(ProductCreateRequest productCreateRequest) {

    Product product = productMapper.toProduct(productCreateRequest);

    Optional<Category> category = categoryRepo.findById(productCreateRequest.categoryId());

    if (!category.isPresent()) {
      throw new TargetNotFound("Category : " + productCreateRequest.categoryId() + " not found");
    }
    product.setCategory(category.get());
    product = productRepo.save(product);
    ProductViewDTO resp = productMapper.toProductViewDTO(product);
    log.info("product created {}", resp);
    return resp;
  }

  public ProductViewDTO updateProduct(ProductUpdateRequest productUpdateRequest, long id) {
    Product product =
        productRepo.findById(id).orElseThrow(() -> new TargetNotFound("Product Id : " + id));

    Product originalCopy = productMapper.clone(product);

    productMapper.updateFromRequest(productUpdateRequest, product);

    if (productUpdateRequest.categoryId() != null) {

      Category category =
          categoryRepo
              .findById(productUpdateRequest.categoryId())
              .orElseThrow(
                  () ->
                      new TargetNotFound(
                          "Category : " + productUpdateRequest.categoryId() + " not found"));

      product.setCategory(category);
    }

    // Use manual comparison to check if any changes were made
    if (ProductComparison.hasNoChanges(originalCopy, product, productUpdateRequest)) {
      throw new IllegalArgumentException("no changes");
    }
    product = productRepo.save(product);
    ProductViewDTO resp = productMapper.toProductViewDTO(product);
    log.info("product updated {}", resp);
    return resp;
  }

  public void deleteProduct(long id) {
    if (!productRepo.existsById(id)) {
      throw new TargetNotFound("Product Id : " + id);
    }
    log.info("product deleted {}", id);
    productRepo.deleteById(id);
  }

  public Product getProduct(Long id) {
    Optional<Product> product = productRepo.findById(id);
    if (product.isEmpty()) {
      throw new TargetNotFound("Product : " + id + " not found");
    }
    log.info("product requested as entity and returned {}", product.get().getId());
    return product.get();
  }
}
