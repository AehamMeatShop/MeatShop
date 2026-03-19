package com.Market.MeatShop.Products.Controllers;


import com.Market.MeatShop.Products.DTOs.Requests.ProductCreateRequest;
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

}
