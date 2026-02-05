package com.doapp.qal.dto;

import java.time.LocalDate;
import java.util.List;

public record QalDto(
    String id,
    String qalNumber,
    LocalDate qalDate,
    String spkNumber,
    String jobName,
    String adminCode,
    String adminName,
    String adminPosition,
    String driverCode,
    String driverName,
    String customerCode,
    String customerName,
    String status,
    List<QalDetailDto> details
) {}
