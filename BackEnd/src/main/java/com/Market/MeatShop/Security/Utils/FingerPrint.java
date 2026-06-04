package com.Market.MeatShop.Security.Utils;

public record FingerPrint(
    String ip, String DID, String screenResolution, String os, String osVersion, String browser) {}
