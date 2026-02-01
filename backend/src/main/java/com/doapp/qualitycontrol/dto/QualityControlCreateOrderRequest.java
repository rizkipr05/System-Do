package com.doapp.qualitycontrol.dto;

import java.util.List;

public record QualityControlCreateOrderRequest(
    Long customerId,
    Long addressId,
    String note,
    List<Item> items
) {
  public record Item(Long productId, int quantity) {}
}
