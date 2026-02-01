package com.doapp.qal.dto;

public record ProfileDto(
    String role,
    Long userId,
    String name,
    String email,
    String phone,
    String code,
    String position
) {}
