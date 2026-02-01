package com.doapp.qal.dto;

public record ProjectControlProfileDto(
    Long id,
    Long userId,
    String name,
    String email,
    String pcCode
) {}
