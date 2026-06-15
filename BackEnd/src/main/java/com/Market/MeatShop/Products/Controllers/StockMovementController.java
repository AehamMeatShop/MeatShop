package com.Market.MeatShop.Products.Controllers;

import com.Market.MeatShop.Products.DTOs.Requests.BaseStockMoveReq;
import com.Market.MeatShop.Products.Handlers.StockMovementHandler;
import com.Market.MeatShop.Products.Services.StockMovementService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/stock-movements")
public class StockMovementController {
  private final StockMovementService stockMovementService;
  private List<StockMovementHandler> stockMovementHandlers;

  public StockMovementController(
      StockMovementService stockMovementService, List<StockMovementHandler> stockMovementHandlers) {
    this.stockMovementService = stockMovementService;
    this.stockMovementHandlers = stockMovementHandlers;
  }

  @PreAuthorize("permitAll()")
  @GetMapping("/health-check")
  public ResponseEntity<?> healthCheck() {
    log.info("GET /stock-movements requested");
    return ResponseEntity.ok().build();
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/product/{id}/stock")
  public ResponseEntity<?> getCurrentProduct(@PathVariable Long id) {
    log.info("GET /stock-movements/product/{}/stock requested", id);
    return ResponseEntity.status(HttpStatus.OK)
        .body(stockMovementService.getCurrentStockOfProd(id));
  }

  @PreAuthorize("hasAuthority('STOCK_MANAGEMENT')")
  @PostMapping("")
  public ResponseEntity<?> adjustmentProdStock2(@Valid @RequestBody BaseStockMoveReq req) {
    log.info("POST /stock-movements {} requested", req);
    StockMovementHandler handler =
        stockMovementHandlers.stream()
            .filter(h -> h.canHandle(req.getType()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid movement type"));

    return ResponseEntity.status(HttpStatus.CREATED).body(handler.handle(req));
  }
}
