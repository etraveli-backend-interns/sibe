package com.etraveli.ibe.sibe.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import com.etraveli.ibe.sibe.exception.InvalidRequestException;

/**
 * Query parameters for a round-trip flight search.
 *
 * @param origin      3-letter IATA code of the departure airport
 * @param destination 3-letter IATA code of the arrival airport
 * @param outboundDate the date of the outbound leg
 * @param returnDate   the date of the return leg
 * @param passengers   number of passengers (1-4, default 1)
 * @param sortBy       field to sort by: {@code totalPrice}, {@code outboundPrice}, {@code returnPrice} (default {@code totalPrice})
 * @param sortDir      {@code asc} or {@code desc} (default {@code asc})
 * @param page         zero-based page index (default 0)
 * @param size         page size, 1-100 (default 10)
 */
public record RoundTripSearchRequest(
  @NotNull @Pattern(regexp = "[A-Z]{3}", message = "must be a 3-letter IATA code") String origin,
  @NotNull @Pattern(regexp = "[A-Z]{3}", message = "must be a 3-letter IATA code") String destination,
  @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate outboundDate,
  @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate,
  @Min(1) @Max(4) Integer passengers,
  @Pattern(regexp = "totalPrice|outboundPrice|returnPrice", message = "must be one of: totalPrice, outboundPrice, returnPrice") String sortBy,
  @Pattern(regexp = "(?i)asc|desc", message = "must be asc or desc") String sortDir,
  @Min(0) Integer page,
  @Min(1) @Max(100) Integer size
) {

  public RoundTripSearchRequest {
    if (passengers == null) passengers = 1;
    if (sortBy == null) sortBy = "totalPrice";
    if (sortDir == null) sortDir = "asc";
    if (page == null) page = 0;
    if (size == null) size = 10;
  }

  public void validateDates() {
    if (returnDate.isBefore(outboundDate)) {
      throw new InvalidRequestException("returnDate must not be before outboundDate");
    }
    if (origin.equals(destination)) {
      throw new InvalidRequestException("origin and destination must be different airports");
    }
  }
}
