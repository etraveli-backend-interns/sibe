package com.etraveli.ibe.sibe.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.etraveli.ibe.sibe.dto.FlightResponse;
import com.etraveli.ibe.sibe.dto.FlightSearchRequest;
import com.etraveli.ibe.sibe.dto.PageResponse;
import com.etraveli.ibe.sibe.entities.Flight;
import com.etraveli.ibe.sibe.exception.InvalidRequestException;
import com.etraveli.ibe.sibe.exception.NotFoundException;
import com.etraveli.ibe.sibe.repository.FlightDAO;
import com.etraveli.ibe.sibe.repository.FlightSearchCriteria;
import com.etraveli.ibe.sibe.service.FlightService;

@Service
public class FlightServiceImpl implements FlightService {

  private final FlightDAO flightDAO;

  public FlightServiceImpl(FlightDAO flightDAO) {
    this.flightDAO = flightDAO;
  }

  @Override
  public PageResponse<FlightResponse> search(FlightSearchRequest request) {
    if (request.minPrice() != null && request.maxPrice() != null && request.minPrice().compareTo(request.maxPrice()) > 0) {
      throw new InvalidRequestException("minPrice must not be greater than maxPrice");
    }

    FlightSearchCriteria criteria = new FlightSearchCriteria(
      upperCaseOrNull(request.origin()),
      upperCaseOrNull(request.destination()),
      request.departureDate(),
      request.minPrice(),
      request.maxPrice(),
      request.passengers(),
      LocalDateTime.now(),
      request.sortBy(),
      request.sortDir(),
      request.page() * request.size(),
      request.size()
    );

    List<FlightResponse> items = flightDAO.getFlights(criteria).stream().map(FlightResponse::from).toList();
    long total = flightDAO.countFlights(criteria);
    return PageResponse.of(items, request.page(), request.size(), total);
  }

  @Override
  public FlightResponse getFlight(Long id) {
    Flight flight = flightDAO.getFlight(id);
    if (flight == null) {
      throw new NotFoundException("Flight " + id + " not found");
    }
    return FlightResponse.from(flight);
  }

  private static String upperCaseOrNull(String value) {
    return value == null ? null : value.toUpperCase();
  }
}
