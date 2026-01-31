package com.doapp.driver.dto;

public record DriverOrderItemDto(
    Long productId,
    String productName,
    int quantity
) {}
