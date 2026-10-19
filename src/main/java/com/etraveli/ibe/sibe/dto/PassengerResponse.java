package com.etraveli.ibe.sibe.dto;

import java.time.LocalDate;

import com.etraveli.ibe.sibe.entities.Passenger;

public record PassengerResponse(String firstName, String lastName, LocalDate dateOfBirth, String passportNumber) {

  public static PassengerResponse from(Passenger passenger) {
    return new PassengerResponse(
      passenger.firstName(),
      passenger.lastName(),
      passenger.dateOfBirth(),
      passenger.passportNumber()
    );
  }
}
