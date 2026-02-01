package com.doapp.qal.dto;

import java.time.LocalDate;
import java.util.List;

public record QalDto(
    String id,
    String qalNumber,
    LocalDate qalDate,
    String spkNumber,
    String jobName,
    String qcCode,
    String qcName,
    String qcPosition,
    String projectControlCode,
    String projectControlName,
    String ownerCode,
    String ownerName,
    String status,
    List<QalDetailDto> details
) {}
