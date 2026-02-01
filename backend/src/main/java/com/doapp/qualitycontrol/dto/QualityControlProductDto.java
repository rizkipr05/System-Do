package com.doapp.qualitycontrol.dto;

import java.math.BigDecimal;

public record QualityControlProductDto(
    Long id,
    String name,
    String sku,
    String unit,
    BigDecimal price,
    int stock,
    boolean active
) {}
