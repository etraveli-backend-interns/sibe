package com.etraveli.ibe.sibe.dto;

import java.math.BigDecimal;

/**
 * A round-trip option: one outbound flight and one return flight, with the combined price.
 *
 * @param outbound   the outbound flight
 * @param returnFlight the return flight
 * @param totalPrice outbound price + return price (for a single passenger)
 */
public record FlightPairResponse(
  FlightResponse outbound,
  FlightResponse returnFlight,
  BigDecimal totalPrice
) {

  public static FlightPairResponse of(FlightResponse outbound, FlightResponse returnFlight) {
    BigDecimal total = outbound.price().add(returnFlight.price());
    return new FlightPairResponse(outbound, returnFlight, total);
  }
}
