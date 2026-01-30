package com.doapp.order;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class OrderNumberGenerator {
  private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.BASIC_ISO_DATE;

  public static String generate() {
    String datePart = LocalDate.now().format(DATE_FMT);
    String rand = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
    return "DO-" + datePart + "-" + rand;
  }
}
