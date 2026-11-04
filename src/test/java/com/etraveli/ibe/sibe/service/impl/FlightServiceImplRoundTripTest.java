package com.etraveli.ibe.sibe.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etraveli.ibe.sibe.dto.FlightPairResponse;
import com.etraveli.ibe.sibe.dto.FlightResponse;
import com.etraveli.ibe.sibe.dto.PageResponse;
import com.etraveli.ibe.sibe.dto.RoundTripSearchRequest;
import com.etraveli.ibe.sibe.entities.Flight;
import com.etraveli.ibe.sibe.exception.InvalidRequestException;
import com.etraveli.ibe.sibe.repository.FlightDAO;
import com.etraveli.ibe.sibe.repository.FlightSearchCriteria;

@ExtendWith(MockitoExtension.class)
class FlightServiceImplRoundTripTest {

  @Mock
  FlightDAO flightDAO;

  @InjectMocks
  FlightServiceImpl flightService;

  private static Flight sampleFlight(long id, String number, String airline, String origin, String dest,
                                     LocalDateTime dep, LocalDateTime arr, BigDecimal price, int seats) {
    return new Flight(id, number, airline, origin, dest, dep, arr, price, "EUR", seats, seats);
  }

  @Test
  void roundTrip_returnsPairedFlightsWithTotalPrice() {
    LocalDate outDate = LocalDate.now().plusDays(3);
    LocalDate retDate = LocalDate.now().plusDays(10);

    // Outbound: ATH -> LON
    List<Flight> outFlights = List.of(
      sampleFlight(1, "A3600", "Aegean", "ATH", "LON",
        outDate.atTime(7, 15), outDate.atTime(9, 30), new BigDecimal("189.90"), 180),
      sampleFlight(2, "BA641", "British", "ATH", "LON",
        outDate.atTime(12, 40), outDate.atTime(15, 5), new BigDecimal("249.00"), 150)
    );
    // Return: LON -> ATH
    List<Flight> retFlights = List.of(
      sampleFlight(7, "A3601", "Aegean", "LON", "ATH",
        retDate.atTime(16, 30), retDate.atTime(21, 45), new BigDecimal("175.00"), 180),
      sampleFlight(6, "BA643", "British", "LON", "ATH",
        retDate.atTime(10, 0), retDate.atTime(15, 20), new BigDecimal("235.00"), 150)
    );

    // The DAO is called twice: once for outbound, once for return.
    // We distinguish by origin of the criteria.
    when(flightDAO.getFlights(any(FlightSearchCriteria.class)))
      .thenAnswer(invocation -> {
        FlightSearchCriteria c = invocation.getArgument(0);
        return "ATH".equals(c.origin()) ? outFlights : retFlights;
      });

    RoundTripSearchRequest request = new RoundTripSearchRequest(
      "ATH", "LON", outDate, retDate, 2, "totalPrice", "asc", 0, 100
    );

    PageResponse<FlightPairResponse> result = flightService.searchRoundTrip(request);

    // 2 outbound x 2 return = 4 pairs
    assertEquals(4, result.totalElements());
    assertEquals(4, result.items().size());
    assertEquals(1, result.totalPages());

    // Cheapest pair: Aegean + Aegean = 189.90 + 175.00 = 364.90
    FlightPairResponse cheapest = result.items().get(0);
    assertEquals(1L, cheapest.outbound().id());
    assertEquals(7L, cheapest.returnFlight().id());
    assertEquals(0, new BigDecimal("364.90").compareTo(cheapest.totalPrice()));

    // Most expensive pair: British + British = 249.00 + 235.00 = 484.00
    FlightPairResponse priciest = result.items().get(3);
    assertEquals(2L, priciest.outbound().id());
    assertEquals(6L, priciest.returnFlight().id());
    assertEquals(0, new BigDecimal("484.00").compareTo(priciest.totalPrice()));
  }

  @Test
  void roundTrip_pagingWorks() {
    LocalDate outDate = LocalDate.now().plusDays(3);
    LocalDate retDate = LocalDate.now().plusDays(10);

    List<Flight> outFlights = List.of(
      sampleFlight(1, "A3600", "Aegean", "ATH", "LON",
        outDate.atTime(7, 15), outDate.atTime(9, 30), new BigDecimal("189.90"), 180),
      sampleFlight(2, "BA641", "British", "ATH", "LON",
        outDate.atTime(12, 40), outDate.atTime(15, 5), new BigDecimal("249.00"), 150),
      sampleFlight(3, "FR9011", "Ryanair", "ATH", "LON",
        outDate.atTime(6, 0), outDate.atTime(8, 15), new BigDecimal("79.99"), 189)
    );
    List<Flight> retFlights = List.of(
      sampleFlight(7, "A3601", "Aegean", "LON", "ATH",
        retDate.atTime(16, 30), retDate.atTime(21, 45), new BigDecimal("175.00"), 180)
    );

    when(flightDAO.getFlights(any(FlightSearchCriteria.class)))
      .thenAnswer(invocation -> {
        FlightSearchCriteria c = invocation.getArgument(0);
        return "ATH".equals(c.origin()) ? outFlights : retFlights;
      });

    // 3 outbound x 1 return = 3 pairs, request page 1, size 2
    RoundTripSearchRequest request = new RoundTripSearchRequest(
      "ATH", "LON", outDate, retDate, 1, "totalPrice", "asc", 1, 2
    );

    PageResponse<FlightPairResponse> result = flightService.searchRoundTrip(request);

    // 3 total, page 1 with size 2 -> 1 item remaining
    assertEquals(3, result.totalElements());
    assertEquals(1, result.items().size());
    assertEquals(2, result.totalPages());
  }

  @Test
  void roundTrip_returnDateBeforeOutboundDate_throwsInvalidRequest() {
    RoundTripSearchRequest request = new RoundTripSearchRequest(
      "ATH", "LON", LocalDate.now().plusDays(10), LocalDate.now().plusDays(3), 1, null, null, null, null
    );

    assertThrows(InvalidRequestException.class, () -> flightService.searchRoundTrip(request));
  }

  @Test
  void roundTrip_sameOriginAndDestination_throwsInvalidRequest() {
    RoundTripSearchRequest request = new RoundTripSearchRequest(
      "ATH", "ATH", LocalDate.now().plusDays(3), LocalDate.now().plusDays(10), 1, null, null, null, null
    );

    assertThrows(InvalidRequestException.class, () -> flightService.searchRoundTrip(request));
  }

  @Test
  void roundTrip_emptyResults() {
    LocalDate outDate = LocalDate.now().plusDays(3);
    LocalDate retDate = LocalDate.now().plusDays(10);

    when(flightDAO.getFlights(any(FlightSearchCriteria.class))).thenReturn(List.of());

    RoundTripSearchRequest request = new RoundTripSearchRequest(
      "ATH", "LON", outDate, retDate, 1, null, null, null, null
    );

    PageResponse<FlightPairResponse> result = flightService.searchRoundTrip(request);

    assertTrue(result.items().isEmpty());
    assertEquals(0, result.totalElements());
    assertEquals(0, result.totalPages());
  }
}
