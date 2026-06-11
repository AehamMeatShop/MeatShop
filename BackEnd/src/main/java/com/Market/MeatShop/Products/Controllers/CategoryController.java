package com.Market.MeatShop.Products.Controllers;

import com.Market.MeatShop.Products.DTOs.CategoryViewDTO;
import com.Market.MeatShop.Products.DTOs.Requests.CategoryCreateRequest;
import com.Market.MeatShop.Products.Services.CategoryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@Slf4j
public class CategoryController {

  private CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @PreAuthorize("hasAuthority('CATEGORY_MANAGEMENT')")
  @PostMapping
  public ResponseEntity<?> createCategory(
      @RequestBody @Valid CategoryCreateRequest categoryCreateRequest) {
    log.info("POST /categories {}", categoryCreateRequest);
    CategoryViewDTO categoryViewDTO;

    categoryViewDTO = categoryService.createCategory(categoryCreateRequest);

    return ResponseEntity.status(HttpStatus.CREATED).body(categoryViewDTO);
  }

  @GetMapping("/health-check")
  public ResponseEntity<?> healthCheck() {
    log.info("GET /categories/health-check requested");
    return ResponseEntity.ok().build();
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping
  public ResponseEntity<?> viewAllCategories() {

    log.info("GET /categories");
    return ResponseEntity.ok().body(categoryService.viewAllCategories());
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/{id}")
  public ResponseEntity<?> viewCategoryById(@PathVariable long id) {
    log.info("GET /categories/{}", id);
    return ResponseEntity.status(HttpStatus.OK).body(categoryService.viewCategoryById(id));
  }

  @PreAuthorize("hasAuthority('CATEGORY_MANAGEMENT')")
  @PutMapping("/{id}")
  public ResponseEntity<?> updateCategory(
      @PathVariable long id, @RequestBody @Valid CategoryCreateRequest categoryCreateRequest) {
    log.info("PUT /categories/{}  {} ", id, categoryCreateRequest);
    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body(categoryService.updateCategory(categoryCreateRequest, id));
  }

  @PreAuthorize("hasAuthority('CATEGORY_MANAGEMENT')")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteCategory(@PathVariable long id) {
    log.info("DELETE /categories/{}", id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(categoryService.deleteCategory(id));
  }
}
