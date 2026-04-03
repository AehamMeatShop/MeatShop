package com.Market.MeatShop.Products.Handlers;
import com.Market.MeatShop.Products.DTOs.Requests.BaseStockMoveReq;
import com.Market.MeatShop.Products.DTOs.Requests.WastShrinkageProdStockReq;
import com.Market.MeatShop.Products.DTOs.StockMoveViewDTO;
import com.Market.MeatShop.Products.Enums.StockMovementsTypes;
import com.Market.MeatShop.Products.Services.StockMovementService;
import org.springframework.stereotype.Component;

@Component

public class WastShrinkageStockMoveHandler extends StockMovementHandler {

    public WastShrinkageStockMoveHandler(StockMovementService service) {
        super(service);
    }
    @Override
    public boolean canHandle(StockMovementsTypes type){

            return type == StockMovementsTypes.WASTE ||
                    type == StockMovementsTypes.SHRINKAGE;

    }
    public StockMoveViewDTO handle(BaseStockMoveReq req){
        return this.service.wastedShrinkageProductStock((WastShrinkageProdStockReq) req);
    }
}
