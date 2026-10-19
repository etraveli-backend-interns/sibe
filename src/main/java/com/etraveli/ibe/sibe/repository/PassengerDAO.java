package com.etraveli.ibe.sibe.repository;

import java.util.List;

import com.etraveli.ibe.sibe.entities.Passenger;

public interface PassengerDAO {

  void insertPassengers(long bookingId, List<Passenger> passengers);

  /** Returns the passengers of the booking, not null. */
  List<Passenger> getPassengers(long bookingId);

  int countPassengers(long bookingId);
}
