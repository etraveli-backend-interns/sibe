package com.etraveli.ibe.sibe.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Flight(
  Long id,
  String flightNumber,
  String airline,
  String origin,
  String destination,
  LocalDateTime departureTime,
  LocalDateTime arrivalTime,
  BigDecimal price,
  String currency,
  int totalSeats,
  int availableSeats
) {}
