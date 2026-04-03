package com.Market.MeatShop.Products.Services;

import com.Market.MeatShop.Finances.Entities.InvoiceComponent;
import com.Market.MeatShop.Finances.Services.InvoiceComponentService;
import com.Market.MeatShop.Products.DTOs.Requests.AdjProdStockReq;
import com.Market.MeatShop.Products.DTOs.Requests.SellPurchaseStockReq;
import com.Market.MeatShop.Products.DTOs.Requests.WastProdStockReq;
import com.Market.MeatShop.Products.DTOs.StockMoveViewDTO;
import com.Market.MeatShop.Products.Entities.Product;
import com.Market.MeatShop.Products.Entities.StockMovment;
import com.Market.MeatShop.Products.Enums.StockMovementsTypes;
import com.Market.MeatShop.Products.Mappers.ProductMapper;
import com.Market.MeatShop.Products.Mappers.StockMovementMapper;
import com.Market.MeatShop.Products.Repositories.ProductRepo;
import com.Market.MeatShop.Products.Repositories.StockMovementRepo;
import com.Market.MeatShop.Shared.Exceptions.QuantityIsNotRegular;
import com.Market.MeatShop.Shared.Exceptions.TargetNotFound;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Optional;

@Service
public class StockMovementService {
    private final ProductRepo productRepo;
    private final ProductMapper productMapper;
    private StockMovementMapper stockMovementMapper;
    private final InvoiceComponentService invoiceComponentService;
    private final StockMovementRepo stockMovementRepo;
    public StockMovementService( ProductRepo productRepo , ProductMapper productMapper ,StockMovementMapper stockMovementMapper,
                                  InvoiceComponentService invoiceComponentService ,StockMovementRepo stockMovementRepo) {
        this.productMapper = productMapper;
        this.productRepo = productRepo;
        this.invoiceComponentService = invoiceComponentService;
        this.stockMovementRepo = stockMovementRepo;
        this.stockMovementMapper = stockMovementMapper;

    }


        public HashMap<String,BigDecimal> getCurrentStockOfProd(Long id){
            Optional<Product> product = productRepo.findById(id);
            if(product.isEmpty() ){
                throw new TargetNotFound("Product : " +id+ " not found");
            }
            BigDecimal currentStock = stockMovementRepo.getCurrentStock(id);

            HashMap<String,BigDecimal> resp = new HashMap<>();
            resp.put("product_id",BigDecimal.valueOf(id));
            resp.put("currentStock",currentStock);

            return resp;

        }

private Product getProduct(Long id){
    Optional<Product> product = productRepo.findById(id);
    if(product.isEmpty() ){
        throw new TargetNotFound("Product : " +id+ " not found");
    }
    return product.get();
}

    @Transactional
    private StockMoveViewDTO createStockMovement(Product product , BigDecimal quantity , String notes , InvoiceComponent component, StockMovementsTypes type) {
        StockMovment stockMovment=new StockMovment();
        stockMovment.setProduct(product);
        stockMovment.setQuantity(quantity);
        stockMovment.setNotes(notes);
        stockMovment.setStockMovementsType(type);
        stockMovment.setInvoiceComponent(component);
        stockMovment=stockMovementRepo.save(stockMovment);

        return stockMovementMapper.toViewDto(stockMovment);
    }

    @Transactional
    public  StockMoveViewDTO adjustmentProductStock(AdjProdStockReq req) {
        Product product = getProduct(req.getProductId());
        if(req.getQuantity().compareTo(BigDecimal.ZERO) == 0){
            throw new IllegalArgumentException("Quantity cannot be zero");
        }
        else if(req.getQuantity().compareTo(BigDecimal.ZERO) < 0){

            BigDecimal currentStock = stockMovementRepo.getCurrentStock(req.getProductId());
            BigDecimal temporalQuantity=req.getQuantity().multiply(BigDecimal.valueOf(-1));
            if(temporalQuantity.compareTo(currentStock) > 0){
                throw new QuantityIsNotRegular("the quantity is greater than the exists stock of product");
            }
        }

        return createStockMovement(product , req.getQuantity() , req.getNotes() , null,StockMovementsTypes.ADJUSTMENT);

    }



    @Transactional
    public  StockMoveViewDTO wastedProductStock(WastProdStockReq req) {
        Product product = getProduct(req.getProductId());
        if(req.getQuantity().compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("the wasted quantity cannot be zero or negative");
        }


        BigDecimal currentStock = stockMovementRepo.getCurrentStock(req.getProductId());

        if(req.getQuantity().compareTo(currentStock) > 0){
            throw new QuantityIsNotRegular("the wasted quantity is greater than the exists stock of product");
        }


      return createStockMovement(product , req.getQuantity().multiply(BigDecimal.valueOf(-1)) , req.getNotes() ,null, StockMovementsTypes.ADJUSTMENT);

    }

    public StockMoveViewDTO sellProductStock(SellPurchaseStockReq req) {
        Product product = getProduct(req.getProductId());

        if(req.getQuantity().compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("the sold quantity cannot be zero or negative");
        }
        BigDecimal currentStock = getCurrentStockOfProd(req.getProductId()).get("currentStock");

        if(req.getQuantity().compareTo(currentStock) > 0){
            throw new IllegalArgumentException("the sold quantity cannot be greater than the exists stock of product : " + currentStock);
        }

        return createStockMovement(product , req.getQuantity().multiply(BigDecimal.valueOf(-1)) , req.getNotes() ,null, StockMovementsTypes.SELL);

    }


}
