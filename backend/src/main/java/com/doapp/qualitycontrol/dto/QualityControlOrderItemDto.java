package com.doapp.qualitycontrol.dto;

import java.math.BigDecimal;

public record QualityControlOrderItemDto(
    Long productId,
    String productName,
    BigDecimal price,
    int quantity
) {}
