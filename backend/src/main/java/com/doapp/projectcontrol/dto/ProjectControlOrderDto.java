package com.doapp.projectcontrol.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ProjectControlOrderDto(
    Long id,
    String doNumber,
    String status,
    LocalDateTime createdAt,
    String customerName,
    String addressLine,
    String addressCity,
    String addressPhone,
    List<ProjectControlOrderItemDto> items
) {}
