package com.etraveli.ibe.sibe.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.etraveli.ibe.sibe.entities.Booking;
import com.etraveli.ibe.sibe.entities.BookingStatus;
import com.etraveli.ibe.sibe.entities.Flight;
import com.etraveli.ibe.sibe.entities.Passenger;
import com.etraveli.ibe.sibe.entities.Payment;

public record BookingResponse(
  String reference,
  BookingStatus status,
  BigDecimal totalAmount,
  String currency,
  String contactEmail,
  LocalDateTime createdAt,
  LocalDateTime updatedAt,
  FlightResponse flight,
  List<PassengerResponse> passengers,
  List<PaymentResponse> payments
) {

  public static BookingResponse from(Booking booking, Flight flight, List<Passenger> passengers, List<Payment> payments) {
    return new BookingResponse(
      booking.reference(),
      booking.status(),
      booking.totalAmount(),
      booking.currency(),
      booking.contactEmail(),
      booking.createdAt(),
      booking.updatedAt(),
      FlightResponse.from(flight),
      passengers.stream().map(PassengerResponse::from).toList(),
      payments.stream().map(PaymentResponse::from).toList()
    );
  }
}
