package com.doapp.admin.dto;

import java.math.BigDecimal;

public record AdminProductDto(
    Long id,
    String name,
    String sku,
    String unit,
    BigDecimal price,
    int stock,
    boolean active
) {}
