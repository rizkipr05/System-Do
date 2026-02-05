package com.doapp.driver.dto;

import java.time.LocalDateTime;
import java.util.List;

public record DriverOrderDto(
    Long id,
    String doNumber,
    String status,
    LocalDateTime createdAt,
    String customerName,
    String addressLine,
    String addressCity,
    String addressPhone,
    List<DriverOrderItemDto> items
) {}
