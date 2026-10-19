package com.etraveli.ibe.sibe.repository;

import java.util.List;

import org.jspecify.annotations.Nullable;

import com.etraveli.ibe.sibe.entities.Flight;

public interface FlightDAO {

  /** Returns one page of the flights matching the criteria. */
  List<Flight> getFlights(FlightSearchCriteria criteria);

  /** Returns how many flights match the criteria, ignoring paging. */
  long countFlights(FlightSearchCriteria criteria);

  /** @return null if there is none for the id. */
  @Nullable
  Flight getFlight(long id);

  /** Atomically takes the seats. @return false, changing nothing, if the flight has too few seats left. */
  boolean reserveSeats(long flightId, int seats);

  void releaseSeats(long flightId, int seats);
}
