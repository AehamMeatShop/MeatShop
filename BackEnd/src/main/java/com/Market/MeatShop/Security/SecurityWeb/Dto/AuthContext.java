package com.Market.MeatShop.Security.SecurityWeb.Dto;

public record AuthContext(
    Long sid, String did, String os, String osVersion, String browser, String screenResolution) {}
