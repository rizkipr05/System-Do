package com.doapp.qualitycontrol.dto;

import java.util.List;
import java.util.Map;

public record QualityControlReportSummaryDto(
    long totalOwners,
    long ordersToday,
    long ordersThisMonth,
    long activeShipments,
    Map<String, Long> statusCounts,
    List<ReportItemDto> topProducts,
    List<ReportItemDto> topOwners
) {}
