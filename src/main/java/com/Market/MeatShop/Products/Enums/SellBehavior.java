package com.Market.MeatShop.Products.Enums;

public enum SellBehavior {
    SIMPLE , //to sell the composite items from there stock directly
    PREPARE, // to sell the composite product from its components only
    COMPLEX, // to sell the composite product from its stock and the reminder prepare
}
