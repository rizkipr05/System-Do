package com.doapp.admin.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AdminOrderDto(
    Long id,
    String doNumber,
    String status,
    String note,
    LocalDateTime createdAt,
    Long customerId,
    String customerName,
    String addressLine,
    String driverName,
    List<AdminOrderItemDto> items
) {}
