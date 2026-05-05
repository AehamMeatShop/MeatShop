package com.Market.MeatShop.Products.Handlers;

import com.Market.MeatShop.Products.DTOs.Requests.BaseStockMoveReq;
import com.Market.MeatShop.Products.DTOs.Requests.SellProdStockReq;
import com.Market.MeatShop.Products.DTOs.Requests.TransformProdStockReq;
import com.Market.MeatShop.Products.DTOs.StockMoveViewDTO;
import com.Market.MeatShop.Products.Enums.StockMovementsTypes;
import com.Market.MeatShop.Products.Services.StockMovementService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransformStockMoveHandler extends StockMovementHandler{
    public TransformStockMoveHandler(StockMovementService service) {
        super(service);
    }
    public boolean canHandle(StockMovementsTypes type){

        return type == StockMovementsTypes.TRANSFORM;

    }
    public List<StockMoveViewDTO> handle(BaseStockMoveReq req){
        return this.service.transformProductStock((TransformProdStockReq) req);
    }
}
