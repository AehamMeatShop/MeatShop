package com.Market.MeatShop.Products.Enums;

public enum StockMovementsTypes {
     PURCHASE
    ,SELL
    ,ADJUSTMENT // to solve the problem of wasted weight without clear cause
    ,TRANSFER // move the products from placa to another now is not important
    ,TRANSFORM // convert the product to another as separate the meat and bons
    ,WASTE // expired meat and same cases
    ,SHRINKAGE // solve the drying and lose the weight with the time and minimal quantity with sell
}
