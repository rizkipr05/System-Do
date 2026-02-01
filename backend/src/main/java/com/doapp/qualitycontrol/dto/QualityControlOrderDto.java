package com.doapp.qualitycontrol.dto;

import java.time.LocalDateTime;
import java.util.List;

public record QualityControlOrderDto(
    Long id,
    String doNumber,
    String status,
    String note,
    LocalDateTime createdAt,
    Long customerId,
    String customerName,
    String addressLine,
    String driverName,
    List<QualityControlOrderItemDto> items
) {}
