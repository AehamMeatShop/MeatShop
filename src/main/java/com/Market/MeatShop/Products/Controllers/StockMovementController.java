package com.Market.MeatShop.Products.Controllers;

import com.Market.MeatShop.Products.DTOs.Requests.BaseStockMoveReq;
import com.Market.MeatShop.Products.Handlers.StockMovementHandler;
import com.Market.MeatShop.Products.Services.StockMovementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stock-movements")
public class StockMovementController {
    private final StockMovementService stockMovementService;
    private List<StockMovementHandler> stockMovementHandlers;
    public StockMovementController(StockMovementService stockMovementService , List<StockMovementHandler> stockMovementHandlers) {
        this.stockMovementService = stockMovementService;
        this.stockMovementHandlers = stockMovementHandlers;
    }

    @GetMapping("/product/{id}/stock")
    public ResponseEntity<?> getCurrentProduct(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(stockMovementService.getCurrentStockOfProd(id));

    }

    @PostMapping("")
    public ResponseEntity<?> adjustmentProdStock2(@Valid @RequestBody BaseStockMoveReq req){
        StockMovementHandler handler = stockMovementHandlers.stream()
                .filter(h -> h.canHandle(req.getType()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid movement type"));

     return ResponseEntity.status(HttpStatus.CREATED).body(handler.handle(req));

    }
}
