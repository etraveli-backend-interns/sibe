package com.etraveli.ibe.sibe.endpoints.impl;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.etraveli.ibe.sibe.dto.BookingResponse;
import com.etraveli.ibe.sibe.dto.BookingSummaryResponse;
import com.etraveli.ibe.sibe.dto.CreateBookingRequest;
import com.etraveli.ibe.sibe.dto.PageResponse;
import com.etraveli.ibe.sibe.endpoints.BookingsApi;
import com.etraveli.ibe.sibe.entities.BookingStatus;
import com.etraveli.ibe.sibe.service.BookingService;

@RestController
public class BookingsApiImpl implements BookingsApi {

  private final BookingService bookingService;

  public BookingsApiImpl(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  @Override
  public ResponseEntity<BookingResponse> createBooking(CreateBookingRequest request) {
    BookingResponse booking = bookingService.createBooking(request);
    //perhaps the location header is redundant. we could just return the reference of the created entity
    URI location = ServletUriComponentsBuilder
      .fromCurrentRequest()
      .path("/{reference}")
      .buildAndExpand(booking.reference())
      .toUri();
    return ResponseEntity.created(location).body(booking);
  }

  @Override
  public BookingResponse getBooking(String reference) {
    return bookingService.getBooking(reference.toUpperCase());
  }

  @Override
  public PageResponse<BookingSummaryResponse> listBookings(BookingStatus status, String contactEmail, int page, int size) {
    return bookingService.listBookings(status, contactEmail, page, size);
  }

  @Override
  public BookingResponse cancelBooking(String reference) {
    return bookingService.cancelBooking(reference.toUpperCase());
  }
}
