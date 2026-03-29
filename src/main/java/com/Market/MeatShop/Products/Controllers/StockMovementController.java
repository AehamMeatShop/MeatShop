package com.Market.MeatShop.Products.Controllers;

import com.Market.MeatShop.Products.DTOs.Requests.AdjusmtentProdStockReq;
import com.Market.MeatShop.Products.Services.StockMovementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stock-movements")
public class StockMovementController {
    private final StockMovementService stockMovementService;

    public StockMovementController(StockMovementService stockMovementService) {
        this.stockMovementService = stockMovementService;
    }

    @PostMapping("/adjusment")
    public ResponseEntity<?> adjustmentProdStock(@Valid @RequestBody AdjusmtentProdStockReq adjusmtentProdStockReq){

        return ResponseEntity.status(HttpStatus.CREATED).body(stockMovementService.adjustmentProductStock(adjusmtentProdStockReq));

    }
}
