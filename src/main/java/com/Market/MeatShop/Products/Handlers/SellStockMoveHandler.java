package com.Market.MeatShop.Products.Handlers;

import com.Market.MeatShop.Products.DTOs.Requests.BaseStockMoveReq;
import com.Market.MeatShop.Products.DTOs.Requests.SellProdStockReq;
import com.Market.MeatShop.Products.DTOs.StockMoveViewDTO;
import com.Market.MeatShop.Products.Enums.StockMovementsTypes;
import com.Market.MeatShop.Products.Services.StockMovementService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SellStockMoveHandler extends StockMovementHandler{

    public SellStockMoveHandler(StockMovementService service) {
        super(service);
    }
    @Override
    public boolean canHandle(StockMovementsTypes type){

        return type == StockMovementsTypes.SELL;

    }
    public List<StockMoveViewDTO> handle(BaseStockMoveReq req){
        return this.service.sellProductStock((SellProdStockReq) req);
    }
}
