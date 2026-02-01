package com.doapp.qal.dto;

public record QcProfileDto(
    Long id,
    Long userId,
    String name,
    String email,
    String qcCode,
    String position
) {}
