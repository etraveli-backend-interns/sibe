package com.etraveli.ibe.sibe.endpoints;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.etraveli.ibe.sibe.dto.BookingResponse;
import com.etraveli.ibe.sibe.dto.BookingSummaryResponse;
import com.etraveli.ibe.sibe.dto.CreateBookingRequest;
import com.etraveli.ibe.sibe.dto.PageResponse;
import com.etraveli.ibe.sibe.entities.BookingStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

@RequestMapping("/bookings")
public interface BookingsApi {

  String REFERENCE_PATTERN = "[A-Za-z0-9]{6}";

  /**
   * {@code POST /bookings}: creates a booking for a flight and its passengers. The request body is JSON.
   *
   * @param request the flight id, contact email and passengers, validated before the method runs
   * @return 201 with the new booking and a {@code Location} header pointing to it; 400 if the request is invalid or
   *         the flight cannot take the passengers; 404 if the flight does not exist
   */
  @PostMapping
  ResponseEntity<BookingResponse> createBooking(@RequestBody CreateBookingRequest request);

  /**
   * {@code GET /bookings/{reference}}: returns the full details of one booking. The reference is not case-sensitive.
   *
   * @param reference the six-character booking reference taken from the URL path
   * @return 200 with the booking; 400 if the reference has the wrong format; 404 if it does not exist
   */
  @GetMapping("/{reference}")
  BookingResponse getBooking(@PathVariable @Pattern(regexp = REFERENCE_PATTERN) String reference);

  /**
   * {@code GET /bookings}: lists bookings, newest first, with optional filters and paging as query parameters.
   *
   * @param status       optional, only bookings in this status
   * @param contactEmail optional, only bookings made with this email
   * @param page         zero-based page number, defaults to 0
   * @param size         bookings per page, 1 to 100, defaults to 10
   * @return 200 with one page of booking summaries; 400 if a parameter is invalid
   */
  @GetMapping
  PageResponse<BookingSummaryResponse> listBookings(
    @RequestParam(required = false) BookingStatus status,
    @RequestParam(required = false) @Email String contactEmail,
    @RequestParam(defaultValue = "0") @Min(0) int page,
    @RequestParam(defaultValue = "0") @Min(1) @Max(100) int size
  );

  /**
   * {@code DELETE /bookings/{reference}}: cancels a booking and releases its seats.
   *
   * @param reference the six-character booking reference taken from the URL path
   * @return 200 with the cancelled booking; 400 if the reference has the wrong format or the booking is already
   *         cancelled; 404 if it does not exist
   */
  @DeleteMapping("/{refrence}")
  BookingResponse cancelBooking(@PathVariable @Pattern(regexp = REFERENCE_PATTERN) String reference);

  // TODO(intern): add PUT /bookings/{reference}/passengers to correct passenger details while the booking is CREATED
}
