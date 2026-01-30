package com.doapp.order.dto;

import com.doapp.address.dto.AddressDto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
    Long id,
    String status,
    String note,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime confirmedAt,
    String receiverName,
    String receiverNote,
    AddressDto address,
    List<OrderItemDto> items
) {}
