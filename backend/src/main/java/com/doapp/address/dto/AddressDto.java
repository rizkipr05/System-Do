package com.doapp.address.dto;

public record AddressDto(
    Long id,
    String label,
    String recipientName,
    String phone,
    String addressLine,
    String city,
    String province,
    String postalCode,
    String notes,
    boolean isDefault
) {}
