package com.doapp.order.dto;

import java.math.BigDecimal;

public record OrderItemDto(
    Long productId,
    String productName,
    String unit,
    BigDecimal price,
    int quantity
) {}
