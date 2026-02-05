package com.doapp.qal.dto;

public record CustomerProfileDto(
    Long id,
    Long userId,
    String name,
    String email,
    String customerCode
) {}
