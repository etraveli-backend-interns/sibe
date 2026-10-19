package com.etraveli.ibe.sibe.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Payment(
  Long id,
  Long bookingId,
  BigDecimal amount,
  String currency,
  PaymentStatus status,
  LocalDateTime createdAt
) {

  public Payment withId(long newId) {
    return new Payment(newId, bookingId, amount, currency, status, createdAt);
  }
}
