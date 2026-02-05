package com.doapp.qal.dto;

public record DriverProfileDto(
    Long id,
    Long userId,
    String name,
    String email,
    String driverCode
) {}
