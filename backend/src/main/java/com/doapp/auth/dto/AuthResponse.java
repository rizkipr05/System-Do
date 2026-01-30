package com.doapp.auth.dto;

public record AuthResponse(String token, String role, UserDto user) {}
