package com.doapp.order.dto;

public record OrderSummaryDto(long activeCount, long completedCount, long inTransitCount) {}
