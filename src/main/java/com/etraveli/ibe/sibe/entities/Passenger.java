package com.etraveli.ibe.sibe.entities;

import java.time.LocalDate;

public record Passenger(
  Long id,
  Long bookingId,
  String firstName,
  String lastName,
  LocalDate dateOfBirth,
  String passportNumber
) {}
