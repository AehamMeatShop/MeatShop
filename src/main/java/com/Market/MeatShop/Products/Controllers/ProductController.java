package com.Market.MeatShop.Products.Controllers;


import com.Market.MeatShop.Products.DTOs.Requests.ProductCreateRequest;
import com.Market.MeatShop.Products.DTOs.Requests.ProductFilterRequest;
import com.Market.MeatShop.Products.DTOs.Requests.ProductUpdateRequest;
import com.Market.MeatShop.Products.Services.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts(){
        return ResponseEntity.status(HttpStatus.OK).body(productService.findAll());
    }

    @PostMapping("")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductCreateRequest productCreateRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productCreateRequest));
    }

    @PutMapping("/{id}")
     public ResponseEntity<?> updateProduct(@Valid @RequestBody ProductUpdateRequest productUpdateRequest , @PathVariable long id){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(productService.updateProduct(productUpdateRequest , id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable long id){
        productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
    @GetMapping("/filter")
    public ResponseEntity<?> getProductsByCategory(ProductFilterRequest filter){
      return ResponseEntity.status(HttpStatus.OK).body(productService.findAllbyCategoryId(filter));
    }

}
