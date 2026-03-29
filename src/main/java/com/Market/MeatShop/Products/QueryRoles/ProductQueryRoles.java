package com.Market.MeatShop.Products.QueryRoles;

import com.Market.MeatShop.Products.Enums.ProductTypes;
import jdk.dynalink.beans.StaticClass;

import java.time.LocalDateTime;
import java.util.List;

public class ProductQueryRoles {
    public static List<String> ALLOWED_SORT_FIELDS= List.of(
           "categoryId" ,  "createdAt" ,
            "productName" , "updatedAt" ,
            "description" ,    "productType"
    );

    public static int maxPageSize=30;
    public static String defaultSortingField="createdAt";
}
