package com.etraveli.ibe.sibe.endpoints.impl;

import com.etraveli.ibe.sibe.endpoints.helpers.ArrivalTimeComparator;
import com.etraveli.ibe.sibe.endpoints.helpers.DepartureTimeComparator;
import com.etraveli.ibe.sibe.endpoints.helpers.PriceComparator;
import com.etraveli.ibe.sibe.entities.Flight;
import com.etraveli.ibe.sibe.entities.SearchFlightsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import com.etraveli.ibe.sibe.dto.FlightResponse;
import com.etraveli.ibe.sibe.dto.FlightSearchRequest;
import com.etraveli.ibe.sibe.dto.PageResponse;
import com.etraveli.ibe.sibe.endpoints.FlightsApi;
import com.etraveli.ibe.sibe.service.FlightService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
public class FlightsApiImpl implements FlightsApi {

  private static final Logger log = LoggerFactory.getLogger(FlightsApiImpl.class);
  private final FlightService flightService;

  public FlightsApiImpl(FlightService flightService) {
    this.flightService = flightService;
  }

  @Override
  public PageResponse<FlightResponse> searchFlights(FlightSearchRequest request) {
    SearchFlightsResponse response = flightService.search(request);
    List<Flight> sortedFlights = new ArrayList<>();


    switch(request.sortBy()){

      case "price":
        sortedFlights = sortFlights(response.getListOfFlights(),new PriceComparator());
        break;
      case "arrivalTime":
        sortedFlights = sortFlights(response.getListOfFlights(),new ArrivalTimeComparator());
        break;
      case "departureTime" :
        sortedFlights = sortFlights(response.getListOfFlights(),new DepartureTimeComparator());
        break;
      default:
        log.warn("Warning: Unknown sorting parameter.");
    }
    return PageResponse.of((sortedFlights).stream().map(FlightResponse::from).toList(), request.page(), request.size(), response.getTotalFlights());

  }

  @Override
  public FlightResponse getFlight(Long id) {
    return flightService.getFlight(id);
  }

  private List<Flight> sortFlights(List<Flight> unsortedListOfFlights, Comparator<Flight> comparator){
    return unsortedListOfFlights.stream().sorted(comparator).toList();
  }

}
