package com.doapp.qal.dto;

public record UserCreateRequest(
    String name,
    String email,
    String phone,
    String password
) {}
