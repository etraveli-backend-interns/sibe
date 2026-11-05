package com.etraveli.ibe.sibe.endpoints.impl;

import com.etraveli.ibe.sibe.entities.Flight;
import com.etraveli.ibe.sibe.entities.SearchFlightsResponse;
import org.springframework.web.bind.annotation.RestController;

import com.etraveli.ibe.sibe.dto.FlightResponse;
import com.etraveli.ibe.sibe.dto.FlightSearchRequest;
import com.etraveli.ibe.sibe.dto.PageResponse;
import com.etraveli.ibe.sibe.endpoints.FlightsApi;
import com.etraveli.ibe.sibe.service.FlightService;

import java.util.List;

@RestController
public class FlightsApiImpl implements FlightsApi {

  private final FlightService flightService;

  public FlightsApiImpl(FlightService flightService) {
    this.flightService = flightService;
  }

  @Override
  public PageResponse<FlightResponse> searchFlights(FlightSearchRequest request) {
    SearchFlightsResponse response = flightService.search(request);

    return PageResponse.of(response.getListOfFlights().stream().map(FlightResponse::from).toList(), request.page(), request.size(), response.getTotalFlights());

  }

  @Override
  public FlightResponse getFlight(Long id) {
    return flightService.getFlight(id);
  }
}
