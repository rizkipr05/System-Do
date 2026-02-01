package com.doapp.qal.dto;

public record OwnerProfileDto(
    Long id,
    Long userId,
    String name,
    String email,
    String ownerCode
) {}
