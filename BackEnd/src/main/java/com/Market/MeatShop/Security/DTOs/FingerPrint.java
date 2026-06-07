package com.Market.MeatShop.Security.DTOs;

public record FingerPrint(
    String ip, String DID, String screenResolution, String os, String osVersion, String browser) {}
