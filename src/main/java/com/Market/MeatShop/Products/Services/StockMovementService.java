package com.Market.MeatShop.Products.Services;

import com.Market.MeatShop.Finances.Services.InvoiceComponentService;
import com.Market.MeatShop.Products.DTOs.Requests.AdjProdStockReq;
import com.Market.MeatShop.Products.DTOs.Requests.WastProdStockReq;
import com.Market.MeatShop.Products.DTOs.StockMoveViewDTO;
import com.Market.MeatShop.Products.Entities.Product;
import com.Market.MeatShop.Products.Entities.StockMovment;
import com.Market.MeatShop.Products.Enums.StockMovmentsTypes;
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




    @Transactional
    public  StockMoveViewDTO adjustmentProductStock(AdjProdStockReq req) {
        Optional<Product> product = productRepo.findById(req.getProductId());
        if(product.isEmpty() ){
            throw new TargetNotFound("Product : " +req.getProductId()+ " not found");
        }
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
        StockMovment stockMovment=new StockMovment();
        stockMovment.setProduct(product.get());
        stockMovment.setQuantity(req.getQuantity());
        stockMovment.setNotes(req.getNotes());
        stockMovment.setStockMovmentsType(StockMovmentsTypes.ADJUSTMENT);

        stockMovment=stockMovementRepo.save(stockMovment);

        return stockMovementMapper.toViewDto(stockMovment);

    }



    @Transactional
    public  StockMoveViewDTO wastedProductStock(WastProdStockReq req) {
        Optional<Product> product = productRepo.findById(req.getProductId());
        if(product.isEmpty() ){
            throw new TargetNotFound("Product : " +req.getProductId()+ " not found");
        }
        if(req.getQuantity().compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("the wasted quantity cannot be zero or negative");
        }


        BigDecimal currentStock = stockMovementRepo.getCurrentStock(req.getProductId());

        if(req.getQuantity().compareTo(currentStock) > 0){
            throw new QuantityIsNotRegular("the wasted quantity is greater than the exists stock of product");
        }

        StockMovment stockMovment=new StockMovment();
        stockMovment.setProduct(product.get());
        stockMovment.setQuantity(req.getQuantity().multiply(BigDecimal.valueOf(-1)));
        stockMovment.setNotes(req.getNotes());
        stockMovment.setStockMovmentsType(StockMovmentsTypes.ADJUSTMENT);

        stockMovment=stockMovementRepo.save(stockMovment);

        return stockMovementMapper.toViewDto(stockMovment);

    }


}
