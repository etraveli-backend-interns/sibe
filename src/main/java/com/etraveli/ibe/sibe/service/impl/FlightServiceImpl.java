package com.etraveli.ibe.sibe.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.etraveli.ibe.sibe.dto.FlightPairResponse;
import com.etraveli.ibe.sibe.dto.FlightResponse;
import com.etraveli.ibe.sibe.dto.FlightSearchRequest;
import com.etraveli.ibe.sibe.dto.PageResponse;
import com.etraveli.ibe.sibe.dto.RoundTripSearchRequest;
import com.etraveli.ibe.sibe.entities.Flight;
import com.etraveli.ibe.sibe.exception.InvalidRequestException;
import com.etraveli.ibe.sibe.exception.NotFoundException;
import com.etraveli.ibe.sibe.repository.FlightDAO;
import com.etraveli.ibe.sibe.repository.FlightSearchCriteria;
import com.etraveli.ibe.sibe.service.FlightService;

import java.util.Comparator;
import java.util.stream.Collectors;

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

  @Override
  public PageResponse<FlightPairResponse> searchRoundTrip(RoundTripSearchRequest request) {
    request.validateDates();

    // Search outbound flights: origin -> destination on outboundDate
    FlightSearchRequest outboundReq = new FlightSearchRequest(
      request.origin(), request.destination(), request.outboundDate(),
      null, null, request.passengers(), "departureTime", "asc", 0, 100
    );
    // Search return flights: destination -> origin on returnDate
    FlightSearchRequest returnReq = new FlightSearchRequest(
      request.destination(), request.origin(), request.returnDate(),
      null, null, request.passengers(), "departureTime", "asc", 0, 100
    );

    List<FlightResponse> outboundFlights = flightDAO.getFlights(
      toCriteria(outboundReq)
    ).stream().map(FlightResponse::from).toList();

    List<FlightResponse> returnFlights = flightDAO.getFlights(
      toCriteria(returnReq)
    ).stream().map(FlightResponse::from).toList();

    // Build all valid pairs, but only keep pairs where both legs have enough seats
    List<FlightPairResponse> allPairs = new ArrayList<>();
    for (FlightResponse out : outboundFlights) {
      for (FlightResponse ret : returnFlights) {
        allPairs.add(FlightPairResponse.of(out, ret));
      }
    }

    // Sort by the requested field
    Comparator<FlightPairResponse> comparator = switch (request.sortBy()) {
      case "outboundPrice" -> Comparator.comparing(p -> p.outbound().price());
      case "returnPrice" -> Comparator.comparing(p -> p.returnFlight().price());
      default -> Comparator.comparing(FlightPairResponse::totalPrice);
    };
    if ("desc".equalsIgnoreCase(request.sortDir())) {
      comparator = comparator.reversed();
    }

    allPairs.sort(comparator);

    // Paginate
    int total = allPairs.size();
    int fromIndex = request.page() * request.size();
    int toIndex = Math.min(fromIndex + request.size(), total);

    List<FlightPairResponse> pageItems;
    if (fromIndex >= total) {
      pageItems = List.of();
    } else {
      pageItems = allPairs.subList(fromIndex, toIndex).stream().collect(Collectors.toList());
    }

    return PageResponse.of(pageItems, request.page(), request.size(), total);
  }

  private FlightSearchCriteria toCriteria(FlightSearchRequest request) {
    return new FlightSearchCriteria(
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
  }

  private static String upperCaseOrNull(String value) {
    return value == null ? null : value.toUpperCase();
  }
}
