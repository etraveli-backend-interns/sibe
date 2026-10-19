package com.etraveli.ibe.sibe.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.etraveli.ibe.sibe.entities.Flight;

public record FlightResponse(
  Long id,
  String flightNumber,
  String airline,
  String origin,
  String destination,
  LocalDateTime departureTime,
  LocalDateTime arrivalTime,
  BigDecimal price,
  String currency,
  int availableSeats
) {

  public static FlightResponse from(Flight flight) {
    return new FlightResponse(
      flight.id(),
      flight.flightNumber(),
      flight.airline(),
      flight.origin(),
      flight.destination(),
      flight.departureTime(),
      flight.arrivalTime(),
      flight.price(),
      flight.currency(),
      flight.availableSeats()
    );
  }
}
