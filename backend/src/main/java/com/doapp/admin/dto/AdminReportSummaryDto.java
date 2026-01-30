package com.doapp.admin.dto;

import java.util.List;
import java.util.Map;

public record AdminReportSummaryDto(
    long totalCustomers,
    long ordersToday,
    long ordersThisMonth,
    long activeShipments,
    Map<String, Long> statusCounts,
    List<ReportItemDto> topProducts,
    List<ReportItemDto> topCustomers
) {}
