package com.etraveli.ibe.sibe.endpoints.impl;

import org.springframework.web.bind.annotation.RestController;

import com.etraveli.ibe.sibe.dto.FlightResponse;
import com.etraveli.ibe.sibe.dto.FlightSearchRequest;
import com.etraveli.ibe.sibe.dto.PageResponse;
import com.etraveli.ibe.sibe.endpoints.FlightsApi;
import com.etraveli.ibe.sibe.service.FlightService;

@RestController
public class FlightsApiImpl implements FlightsApi {

  private final FlightService flightService;

  public FlightsApiImpl(FlightService flightService) {
    this.flightService = flightService;
  }

  @Override
  public PageResponse<FlightResponse> searchFlights(FlightSearchRequest request) {
    return flightService.search(request);
  }

  @Override
  public FlightResponse getFlight(Long id) {
    return flightService.getFlight(id);
  }
}
