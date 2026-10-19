package com.etraveli.ibe.sibe.service;

import com.etraveli.ibe.sibe.dto.FlightResponse;
import com.etraveli.ibe.sibe.dto.FlightSearchRequest;
import com.etraveli.ibe.sibe.dto.PageResponse;
import com.etraveli.ibe.sibe.exception.InvalidRequestException;
import com.etraveli.ibe.sibe.exception.NotFoundException;

public interface FlightService {

  /**
   * Searches flights by the given filters and returns one page of the result.
   * Only flights that have not departed yet and still have enough free seats for the requested number of passengers
   * are returned. Origin and destination are matched case-insensitively.
   *
   * @param request filters, sorting and paging
   * @return the requested page of flights, with the total number of matches and pages
   * @throws InvalidRequestException if the minimum price is greater than the maximum price
   */
  PageResponse<FlightResponse> search(FlightSearchRequest request);

  /**
   * Returns the flight with the given id.
   *
   * @param id the flight id
   * @return the flight, including how many seats are still available
   * @throws NotFoundException if there is no flight with this id
   */
  FlightResponse getFlight(Long id);
}
