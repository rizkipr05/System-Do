package com.doapp.projectcontrol.dto;

public record ProjectControlOrderItemDto(
    Long productId,
    String productName,
    int quantity
) {}
