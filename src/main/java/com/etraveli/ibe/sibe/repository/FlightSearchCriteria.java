package com.etraveli.ibe.sibe.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record FlightSearchCriteria(
  String origin,
  String destination,
  LocalDate departureDate,
  BigDecimal minPrice,
  BigDecimal maxPrice,
  int minAvailableSeats,
  LocalDateTime departingAfter,
  String sortBy,
  String sortDir,
  int offset,
  int limit
) {}
