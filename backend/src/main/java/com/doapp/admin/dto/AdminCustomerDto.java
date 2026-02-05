package com.doapp.admin.dto;

public record AdminCustomerDto(
    Long userId,
    Long customerId,
    String name,
    String email,
    String phone,
    String companyName,
    boolean active
) {}
