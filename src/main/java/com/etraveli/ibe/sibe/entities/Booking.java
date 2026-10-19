package com.etraveli.ibe.sibe.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Booking(
  Long id,
  String reference,
  Long flightId,
  BookingStatus status,
  BigDecimal totalAmount,
  String currency,
  String contactEmail,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {

  public Booking withId(long newId) {
    return new Booking(newId, reference, flightId, status, totalAmount, currency, contactEmail, createdAt, updatedAt);
  }
}
