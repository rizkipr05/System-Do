package com.doapp.admin.dto;

import java.math.BigDecimal;

public record AdminOrderItemDto(
    Long productId,
    String productName,
    BigDecimal price,
    int quantity
) {}
