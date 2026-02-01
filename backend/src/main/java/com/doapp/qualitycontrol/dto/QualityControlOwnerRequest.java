package com.doapp.qualitycontrol.dto;

public record QualityControlOwnerRequest(
    String name,
    String email,
    String phone,
    String password,
    String companyName,
    Boolean active
) {}
