package com.doapp.qal.dto;

import java.time.LocalDate;
import java.util.List;

public record QalCreateRequest(
    String qalNumber,
    LocalDate qalDate,
    String spkNumber,
    String jobName,
    Long projectControlUserId,
    Long ownerUserId,
    List<QalDetailDto> details
) {}
