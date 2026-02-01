package com.doapp.qal.dto;

public record UserLiteDto(
    Long id,
    String name,
    String email,
    String role
) {}
