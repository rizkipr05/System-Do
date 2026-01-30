package com.doapp.order.dto;

public record ConfirmRequest(
    String receiverName,
    String note,
    String signatureData,
    String proofImageData
) {}
