package com.Market.MeatShop.Parties.QueryRoles;

import java.util.List;

public class PartyQueryRoles {
    public static List<String> ALLOWED_SORT_FIELDS= List.of(
            "partyName" ,  " fromCreatedAt" ,
            "partyAddress" , "fromUpdatedAt" ,
           " toCreatedAt ",  "toUpdatedAt "


    );
    public static int maxPageSize=30;
    public static String defaultSortingField="createdAt";
}
