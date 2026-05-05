package com.Market.MeatShop.Products.Handlers;

import com.Market.MeatShop.Products.DTOs.Requests.BaseStockMoveReq;
import com.Market.MeatShop.Products.DTOs.StockMoveViewDTO;
import com.Market.MeatShop.Products.Enums.StockMovementsTypes;
import com.Market.MeatShop.Products.Services.StockMovementService;

public abstract class StockMovementHandler {
    protected final StockMovementService service;
    public StockMovementHandler(StockMovementService service) { this.service = service; }
    public abstract boolean canHandle(StockMovementsTypes type);
    public abstract Object handle(BaseStockMoveReq request);
}
