package com.doapp.projectcontrol.dto;

public record ProjectControlUpdateStatusRequest(
    String status,
    String note,
    String proofImageData,
    String signatureData
) {}
