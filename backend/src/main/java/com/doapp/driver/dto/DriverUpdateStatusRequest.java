package com.doapp.driver.dto;

public record DriverUpdateStatusRequest(
    String status,
    String note,
    String proofImageData,
    String signatureData
) {}
