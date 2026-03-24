package com.Market.MeatShop.Products.Controllers;

import com.Market.MeatShop.Products.DTOs.Requests.CreateProdCompsRequest;
import com.Market.MeatShop.Products.Entities.ProductComponent;
import com.Market.MeatShop.Products.Services.ProductComponentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/components")
public class ProductComponentController {
    private final ProductComponentService productComponentService;

    public ProductComponentController(ProductComponentService productComponentService) {
        this.productComponentService = productComponentService;
    }

    @PostMapping
    public ResponseEntity<?> createProductComponent(@Valid @RequestBody CreateProdCompsRequest createProdCompsRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productComponentService.createProductComponentList(createProdCompsRequest));
    }
}
