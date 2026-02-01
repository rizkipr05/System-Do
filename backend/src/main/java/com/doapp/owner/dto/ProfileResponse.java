package com.doapp.owner.dto;

public record ProfileResponse(
    Long userId,
    String name,
    String email,
    String phone,
    String customerCode,
    String companyName
) {}
