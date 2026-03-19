package com.Market.MeatShop.Products.Controllers;


import com.Market.MeatShop.Products.DTOs.CategoryViewDTO;
import com.Market.MeatShop.Products.DTOs.Requests.CategoryCreateRequest;
import com.Market.MeatShop.Products.Services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private CategoryService categoryService;
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody @Valid CategoryCreateRequest categoryCreateRequest)
    {
         CategoryViewDTO categoryViewDTO;

            categoryViewDTO = categoryService.createCategory(categoryCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(categoryViewDTO);
    }

    @GetMapping
    public ResponseEntity<?> viewAllCategories() {
        return ResponseEntity.ok(categoryService.viewAllCategories());
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> viewCategoryById(@PathVariable long id) {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.viewCategoryById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable long id, @RequestBody @Valid CategoryCreateRequest categoryCreateRequest){
       return ResponseEntity.status(HttpStatus.ACCEPTED).body(categoryService.updateCategory(categoryCreateRequest,id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable long id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(categoryService.deleteCategory(id));
    }

}
