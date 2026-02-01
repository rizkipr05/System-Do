package com.doapp.qualitycontrol.dto;

public record QualityControlOwnerDto(
    Long userId,
    Long customerId,
    String name,
    String email,
    String phone,
    String companyName,
    boolean active
) {}
