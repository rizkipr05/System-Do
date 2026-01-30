package com.doapp.admin.dto;

import java.util.List;

public record AdminCreateOrderRequest(
    Long customerId,
    Long addressId,
    String note,
    List<Item> items
) {
  public record Item(Long productId, int quantity) {}
}
