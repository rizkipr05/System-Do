package com.doapp.qal.dto;

import java.time.LocalDate;

public record QalDetailDto(
    Long id,
    String documentName,
    String documentType,
    LocalDate receivedDate,
    String verificationStatus
) {}
