package com.etraveli.ibe.sibe.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record FlightSearchRequest(
  @Pattern(regexp = "[A-Z]{3}", message = "must be a 3-letter IATA code") String origin,
  @Pattern(regexp = "[A-Za-z]{3}", message = "must be a 3-letter IATA code") String destination,
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
  @DecimalMin("0") BigDecimal minPrice,
  @DecimalMin("0") BigDecimal maxPrice,
  @Min(1) @Max(4) Integer passengers,
  @Pattern(regexp = "price|departureTime|arrivalTime", message = "must be one of: price, departureTime, arrivalTime") String sortBy,
  @Pattern(regexp = "(?i)asc|desc", message = "must be asc or desc") String sortDir,
  @Min(0) Integer page,
  @Min(1) @Max(100) Integer size
) {

  public FlightSearchRequest {
    if (passengers == null) passengers = 1;
    if (sortBy == null) sortBy = "price";
    if (sortDir == null) sortDir = "asc";
    if (page == null) page = 0;
    if (size == null) size = 10;
  }
}
