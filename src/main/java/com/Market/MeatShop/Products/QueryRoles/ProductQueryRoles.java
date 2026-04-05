package com.Market.MeatShop.Products.QueryRoles;


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
