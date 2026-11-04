package com.etraveli.ibe.sibe.endpoints;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.etraveli.ibe.sibe.dto.FlightPairResponse;
import com.etraveli.ibe.sibe.dto.FlightResponse;
import com.etraveli.ibe.sibe.dto.FlightSearchRequest;
import com.etraveli.ibe.sibe.dto.PageResponse;
import com.etraveli.ibe.sibe.dto.RoundTripSearchRequest;

import jakarta.validation.Valid;

@RequestMapping("/flights")
public interface FlightsApi {

  /**
   * {@code GET /flights}: searches flights. Filters (origin, destination, departure date, price range, number of
   * passengers), sorting and paging are all passed as query parameters.
   *
   * @param request the query parameters, validated before the method runs
   * @return 200 with one page of matching flights; 400 if a parameter is invalid
   */
  @GetMapping
  PageResponse<FlightResponse> searchFlights(@Valid FlightSearchRequest request);

  /**
   * {@code GET /flights/{id}}: returns one flight.
   *
   * @param id the flight id taken from the URL path
   * @return 200 with the flight; 404 if it does not exist
   */
  @GetMapping("/{id}")
  FlightResponse getFlight(@PathVariable Long id);

  /**
   * {@code GET /flights/round-trip}: searches for round-trip options.
   * Returns pairs of outbound and return flights between two airports, with the combined price.
   *
   * @param request the round-trip search query parameters, validated before the method runs
   * @return 200 with one page of flight pairs; 400 if a parameter is invalid
   */
  @GetMapping("/round-trip")
  PageResponse<FlightPairResponse> searchRoundTrip(@Valid RoundTripSearchRequest request);

  // TODO(intern): flight admin - POST /flights, PUT /flights/{id}, DELETE /flights/{id} (refuse deleting flights that have bookings)
}
