package com.Market.MeatShop.Products.Controllers;

import com.Market.MeatShop.Products.DTOs.Requests.ProductCreateRequest;
import com.Market.MeatShop.Products.DTOs.Requests.ProductFilterRequest;
import com.Market.MeatShop.Products.DTOs.Requests.ProductUpdateRequest;
import com.Market.MeatShop.Products.Services.ProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@Slf4j
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping("/health-check")
  public ResponseEntity<?> healthCheck() {

    log.info("GET /products/helth-check requested");
    return ResponseEntity.ok().build();
  }

  @GetMapping
  public ResponseEntity<?> getAllProducts() {
    log.info("GET /products requested");
    return ResponseEntity.status(HttpStatus.OK).body(productService.findAllProducts());
  }

  @PostMapping("")
  public ResponseEntity<?> createProduct(
      @Valid @RequestBody ProductCreateRequest productCreateRequest) {
    log.info("POST /products {} requested", productCreateRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(productService.createProduct(productCreateRequest));
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateProduct(
      @Valid @RequestBody ProductUpdateRequest productUpdateRequest, @PathVariable long id) {
    log.info("PUT /products/{} requested", id);
    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body(productService.updateProduct(productUpdateRequest, id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteProduct(@PathVariable long id) {
    log.info("DELETE /products/{} requested", id);
    productService.deleteProduct(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
  }

  @GetMapping("/filter")
  public ResponseEntity<?> getProductsByFilter(
      @ModelAttribute ProductFilterRequest filter, Pageable pageable) {
    log.info("GET /products query{} requested", filter);
    return ResponseEntity.status(HttpStatus.OK)
        .body(productService.findAllbyFilter(filter, pageable));
  }
}
