package com.Market.MeatShop.Products.Handlers;
import com.Market.MeatShop.Products.DTOs.Requests.BaseStockMoveReq;
import com.Market.MeatShop.Products.DTOs.Requests.WastProdStockReq;
import com.Market.MeatShop.Products.DTOs.StockMoveViewDTO;
import com.Market.MeatShop.Products.Services.StockMovementService;
import org.springframework.stereotype.Component;

@Component

public class WastStockMoveHandler extends StockMovementHandler {

    public WastStockMoveHandler(StockMovementService service) {
        super(service);
    }
    @Override
    public boolean canHandle(String type){

        return "WAST".equals(type);
    }
    public StockMoveViewDTO handle(BaseStockMoveReq req){
        return this.service.wastedProductStock((WastProdStockReq) req);
    }
}
