package com.Market.MeatShop.Security.SecurityWeb.Dto;

public record AuthContext(
    String did, String os, String osVersion, String browser, String screenResolution) {}
