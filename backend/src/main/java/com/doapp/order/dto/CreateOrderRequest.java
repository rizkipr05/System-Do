package com.doapp.order.dto;

import java.util.List;

public record CreateOrderRequest(
    Long addressId,
    String note,
    List<Item> items
) {
  public record Item(Long productId, int quantity) {}
}
