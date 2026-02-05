package com.doapp.qal.dto;

public record AdminProfileDto(
    Long id,
    Long userId,
    String name,
    String email,
    String adminCode,
    String position
) {}
