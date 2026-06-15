package com.Market.MeatShop.Products.Controllers;

import com.Market.MeatShop.Products.DTOs.Requests.CreateProdCompsRequest;

import com.Market.MeatShop.Products.Services.ProductComponentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/components")
@Slf4j
public class ProductComponentController {
  private final ProductComponentService productComponentService;

  public ProductComponentController(ProductComponentService productComponentService) {
    this.productComponentService = productComponentService;
  }

  @PreAuthorize("permitAll()")
  @GetMapping("/health-check")
  public ResponseEntity<?> healthCheck() {
    log.info("GET /components/helth-check requested");
    return ResponseEntity.ok().build();
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/product/{id}")
  public ResponseEntity<?> getProdCompsById(@PathVariable long id) {
    log.info("GET /components/product/{} requested", id);

    return ResponseEntity.status(HttpStatus.OK).body(productComponentService.findProdComps(id));
  }

  @PreAuthorize("hasAuthority('PRODUCT_MANAGEMENT')")
  @PostMapping
  public ResponseEntity<?> createProductComponent(
      @Valid @RequestBody CreateProdCompsRequest createProdCompsRequest) {
    log.info("POST /components/helth-check {} requested", createProdCompsRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(productComponentService.createProductComponentList(createProdCompsRequest));
  }

  @PreAuthorize("hasAuthority('PRODUCT_MANAGEMENT')")
  @DeleteMapping("/{prod-id}")
  public ResponseEntity<?> deleteProductComponent(@PathVariable("prod-id") long id) {
    log.info("DELETE /components/{} requested", id);
    productComponentService.deleteProductComponents(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
