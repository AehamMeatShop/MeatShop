package com.Market.MeatShop.Products.Services;

import com.Market.MeatShop.Finances.Entities.InvoiceComponent;
import com.Market.MeatShop.Finances.Services.InvoiceComponentService;
import com.Market.MeatShop.Products.DTOs.Requests.AdjusmtentProdStockReq;
import com.Market.MeatShop.Products.DTOs.StockMoveViewDTO;
import com.Market.MeatShop.Products.Entities.Product;
import com.Market.MeatShop.Products.Entities.StockMovment;
import com.Market.MeatShop.Products.Enums.StockMovmentsTypes;
import com.Market.MeatShop.Products.Mappers.CategoryMapper;
import com.Market.MeatShop.Products.Mappers.ProductMapper;
import com.Market.MeatShop.Products.Mappers.StockMovementMapper;
import com.Market.MeatShop.Products.Repositories.CategoryRepo;
import com.Market.MeatShop.Products.Repositories.ProductRepo;
import com.Market.MeatShop.Products.Repositories.StockMovementRepo;
import com.Market.MeatShop.Shared.Exceptions.QuantityIsNotRegular;
import com.Market.MeatShop.Shared.Exceptions.TargetNotFound;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
        @Transactional
        public  StockMoveViewDTO adjustmentProductStock(AdjusmtentProdStockReq req) {
          Optional<Product> product = productRepo.findById(req.productId());
           if(product.isEmpty() ){
               throw new TargetNotFound("Product : " +req.productId()+ " not found");
           }
            if(req.quantity().compareTo(BigDecimal.ZERO) == 0){
                throw new IllegalArgumentException("Quantity cannot be zero");
            }
            else if(req.quantity().compareTo(BigDecimal.ZERO) < 0){

               BigDecimal currentStock = stockMovementRepo.getCurrentStock(req.productId());
                BigDecimal temporalQuantity=req.quantity().multiply(BigDecimal.valueOf(-1));
               if(temporalQuantity.compareTo(currentStock) > 0){
                   throw new QuantityIsNotRegular("the quantity is greater than the exists stock of product");
               }
           }
          StockMovment stockMovment=new StockMovment();
           stockMovment.setProduct(product.get());
           stockMovment.setQuantity(req.quantity());
           stockMovment.setNotes(req.notes());
           stockMovment.setStockMovmentsType(StockMovmentsTypes.ADJUSTMENT);

           stockMovment=stockMovementRepo.save(stockMovment);

           return stockMovementMapper.toViewDto(stockMovment);

        }








}
