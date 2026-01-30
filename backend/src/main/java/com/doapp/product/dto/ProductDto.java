package com.doapp.product.dto;

import java.math.BigDecimal;

public record ProductDto(
    Long id,
    String name,
    String sku,
    String unit,
    BigDecimal price
) {}
