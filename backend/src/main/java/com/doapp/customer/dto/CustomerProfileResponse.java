package com.doapp.customer.dto;

public record CustomerProfileResponse(
    Long userId,
    String name,
    String email,
    String phone,
    String customerCode,
    String companyName
) {}
