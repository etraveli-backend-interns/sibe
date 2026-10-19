package com.etraveli.ibe.sibe.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.etraveli.ibe.sibe.entities.Booking;
import com.etraveli.ibe.sibe.entities.BookingStatus;

public record BookingSummaryResponse(
  String reference,
  BookingStatus status,
  Long flightId,
  BigDecimal totalAmount,
  String currency,
  String contactEmail,
  LocalDateTime createdAt
) {

  public static BookingSummaryResponse from(Booking booking) {
    return new BookingSummaryResponse(
      booking.reference(),
      booking.status(),
      booking.flightId(),
      booking.totalAmount(),
      booking.currency(),
      booking.contactEmail(),
      booking.createdAt()
    );
  }
}
