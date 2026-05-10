package com.Market.MeatShop.Products.Services;

import com.Market.MeatShop.Products.DTOs.Requests.*;
import com.Market.MeatShop.Products.DTOs.StockMoveViewDTO;
import com.Market.MeatShop.Products.Entities.Product;
import com.Market.MeatShop.Products.Entities.ProductComponent;
import com.Market.MeatShop.Products.Entities.StockMovment;
import com.Market.MeatShop.Products.Enums.ProductTypes;
import com.Market.MeatShop.Products.Enums.SellBehavior;
import com.Market.MeatShop.Products.Enums.StockMovementsTypes;
import com.Market.MeatShop.Products.Mappers.StockMovementMapper;
import com.Market.MeatShop.Products.Repositories.StockMovementRepo;
import com.Market.MeatShop.Shared.Exceptions.QuantityIsNotRegular;
import com.Market.MeatShop.Shared.Exceptions.TypeError;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class StockMovementService {
  private final ProductService productService;
  private StockMovementMapper stockMovementMapper;
  private final StockMovementRepo stockMovementRepo;
  private final ProductComponentService productComponentService;

  public StockMovementService(
      ProductService productService,
      StockMovementMapper stockMovementMapper,
      StockMovementRepo stockMovementRepo,
      ProductComponentService productComponentService) {

    this.productService = productService;
    this.productComponentService = productComponentService;
    this.stockMovementRepo = stockMovementRepo;
    this.stockMovementMapper = stockMovementMapper;
  }

  public HashMap<String, BigDecimal> getCurrentStockOfProd(Long id) {
    Product product = productService.getProduct(id);

    BigDecimal currentStock = stockMovementRepo.getCurrentStock(id);

    HashMap<String, BigDecimal> resp = new HashMap<>();
    resp.put("product_id", BigDecimal.valueOf(id));
    resp.put("currentStock", currentStock);

    return resp;
  }

  @Transactional
  private StockMoveViewDTO createStockMovement(
      Product product,
      BigDecimal quantity,
      String notes,
      Long componentId,
      StockMovementsTypes type) {
    StockMovment stockMovment = new StockMovment();
    stockMovment.setProduct(product);
    stockMovment.setQuantity(quantity);
    stockMovment.setNotes(notes);
    stockMovment.setStockMovementsType(type);
    stockMovment.setInvoiceComponentId(componentId);
    stockMovment = stockMovementRepo.save(stockMovment);
    StockMoveViewDTO resp = stockMovementMapper.toViewDto(stockMovment);
    log.info("stock movement {} created", resp);
    return resp;
  }

  @Transactional
  public StockMoveViewDTO adjustmentProductStock(AdjProdStockReq req) {
    Product product = productService.getProduct(req.getProductId());
    if (req.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
      throw new IllegalArgumentException("Quantity cannot be zero");
    } else if (req.getQuantity().compareTo(BigDecimal.ZERO) < 0) {

      BigDecimal currentStock = stockMovementRepo.getCurrentStock(req.getProductId());
      BigDecimal temporalQuantity = req.getQuantity().negate();
      if (temporalQuantity.compareTo(currentStock) > 0) {
        throw new QuantityIsNotRegular("the quantity is greater than the exists stock of product");
      }
    }

    return createStockMovement(
        product, req.getQuantity(), req.getNotes(), null, StockMovementsTypes.ADJUSTMENT);
  }

  @Transactional
  public StockMoveViewDTO wastedShrinkageProductStock(WastShrinkageProdStockReq req) {
    Product product = productService.getProduct(req.getProductId());
    if (req.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("the wasted quantity cannot be zero or negative");
    }

    BigDecimal currentStock = stockMovementRepo.getCurrentStock(req.getProductId());

    if (req.getQuantity().compareTo(currentStock) > 0) {
      throw new QuantityIsNotRegular(
          "the wasted quantity is greater than the exists stock of product");
    }

    return createStockMovement(
        product, req.getQuantity().negate(), req.getNotes(), null, req.getType());
  }

  @Transactional
  private BigDecimal prepareSell(SellProdStockReq req, List<StockMoveViewDTO> resp) {
    List<ProductComponent> components =
        productComponentService.findProdCompsToSys(req.getProductId());
    BigDecimal temporalQuantity = BigDecimal.valueOf(req.getQuantity().doubleValue());
    for (ProductComponent component : components) {
      if (temporalQuantity.compareTo(BigDecimal.ZERO) == 0) {
        break;
      }
      Product prodComp = component.getComponentProduct();
      if (prodComp.getProductType() != ProductTypes.SERVICE) {

        BigDecimal quantityOfComp = req.getQuantity().multiply(component.getRatioInKg());
        BigDecimal currentStock = getCurrentStockOfProd(prodComp.getId()).get("currentStock");
        if (quantityOfComp.compareTo(currentStock) > 0) {
          throw new IllegalArgumentException(
              "the sold quantity in prepare sell of prod: "
                  + prodComp.getId()
                  + " cannot be greater than the exists stock of product : "
                  + currentStock);
        }
        temporalQuantity = temporalQuantity.subtract(quantityOfComp);
        resp.add(
            createStockMovement(
                prodComp,
                quantityOfComp.negate(),
                req.getNotes(),
                req.getComponentId(),
                StockMovementsTypes.SELL));
      }
    }
    return temporalQuantity;
  }

  @Transactional
  public List<StockMoveViewDTO> sellProductStock(SellProdStockReq req) {
    Product product = productService.getProduct(req.getProductId());
    List<StockMoveViewDTO> resp = new ArrayList<>();
    if (req.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("the sold quantity cannot be zero or negative");
    }
    if (product.getProductType() == ProductTypes.SERVICE) {
      throw new TypeError("cannot sell services products !!");
    }
    if (req.getBehavior() == SellBehavior.SIMPLE) {
      BigDecimal currentStock = getCurrentStockOfProd(req.getProductId()).get("currentStock");

      if (req.getQuantity().compareTo(currentStock) > 0) {
        throw new IllegalArgumentException(
            "the sold quantity cannot be greater than the exists stock of product : "
                + currentStock);
      }
      resp.add(
          createStockMovement(
              product,
              req.getQuantity().negate(),
              req.getNotes(),
              req.getComponentId(),
              StockMovementsTypes.SELL));

    } else if (req.getBehavior() == SellBehavior.PREPARE) {
      if (product.getProductType() != ProductTypes.COMPOSITE) {
        throw new TypeError("Product Type is not COMPOSITE");
      }

      if (prepareSell(req, resp).compareTo(BigDecimal.ZERO) != 0) {
        throw new IllegalArgumentException("sell error");
      }
    } else if (req.getBehavior() == SellBehavior.COMPLEX) {
      BigDecimal currentStock = getCurrentStockOfProd(req.getProductId()).get("currentStock");
      BigDecimal reminderQuantity;

      if (req.getQuantity().compareTo(currentStock) <= 0) {
        resp.add(
            createStockMovement(
                product, currentStock.negate(), req.getNotes(), null, StockMovementsTypes.SELL));
      }

      reminderQuantity = req.getQuantity().subtract(currentStock);
      if (reminderQuantity.compareTo(BigDecimal.ZERO) == 0) {
        return resp;
      }
      req.setQuantity(reminderQuantity);
      if (prepareSell(req, resp).compareTo(BigDecimal.ZERO) != 0) {
        throw new IllegalArgumentException(
            "the quantity is greater than the exist stock of product : " + currentStock);
      }
    }

    return resp;
  }

  public StockMoveViewDTO purchaseProductStock(PurchaseProdStockReq req) {
    Product product = productService.getProduct(req.getProductId());
    if (req.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("the purchased quantity cannot be zero or less");
    }

    return createStockMovement(
        product,
        req.getQuantity(),
        req.getNotes(),
        req.getComponentId(),
        StockMovementsTypes.PURCHASE);
  }

  public List<StockMoveViewDTO> transformProductStock(TransformProdStockReq req) {
    List<StockMoveViewDTO> resp = new ArrayList<>();
    for (Map.Entry<Long, BigDecimal> inputEntry : req.getInputs().entrySet()) {
      Product product = productService.getProduct(inputEntry.getKey());

      BigDecimal currentStock = getCurrentStockOfProd(inputEntry.getKey()).get("currentStock");
      if (product.getProductType() != ProductTypes.SERVICE) {
        if (inputEntry.getValue().compareTo(currentStock) > 0) {
          throw new IllegalArgumentException(
              "the sold quantity cannot be greater than the current stock of product :  "
                  + inputEntry.getKey()
                  + ": "
                  + inputEntry.getValue());
        }
      }
      resp.add(
          createStockMovement(
              product,
              inputEntry.getValue().negate(),
              req.getNotes(),
              null,
              StockMovementsTypes.TRANSFORM));
    }
    for (Map.Entry<Long, BigDecimal> outputEntry : req.getOutputs().entrySet()) {
      Product product = productService.getProduct(outputEntry.getKey());
      resp.add(
          createStockMovement(
              product,
              outputEntry.getValue(),
              req.getNotes(),
              null,
              StockMovementsTypes.TRANSFORM));
    }
    return resp;
  }
}
