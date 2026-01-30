package com.doapp.admin.dto;

public record AdminCustomerRequest(
    String name,
    String email,
    String phone,
    String password,
    String companyName,
    Boolean active
) {}
