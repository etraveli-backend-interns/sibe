package com.etraveli.ibe.sibe.service;

import com.etraveli.ibe.sibe.dto.BookingResponse;
import com.etraveli.ibe.sibe.dto.BookingSummaryResponse;
import com.etraveli.ibe.sibe.dto.CreateBookingRequest;
import com.etraveli.ibe.sibe.dto.PageResponse;
import com.etraveli.ibe.sibe.entities.BookingStatus;
import com.etraveli.ibe.sibe.exception.InvalidRequestException;
import com.etraveli.ibe.sibe.exception.NotFoundException;

public interface BookingService {

  /**
   * Creates a booking in status {@code CREATED} for the given flight and passengers.
   * One seat per passenger is reserved on the flight and the total amount is the flight price times the number of
   * passengers. Seat reservation and booking creation succeed or fail together.
   *
   * @param request the flight, the contact email and the passengers
   * @return the new booking, including its generated reference
   * @throws NotFoundException       if the flight does not exist
   * @throws InvalidRequestException if the flight has already departed or has too few free seats
   */
  BookingResponse createBooking(CreateBookingRequest request);

  /**
   * Returns the full details of a booking: the flight, the passengers and all payment attempts.
   *
   * @param reference the booking reference
   * @return the booking details
   * @throws NotFoundException if there is no booking with this reference
   */
  BookingResponse getBooking(String reference);

  /**
   * Lists bookings, newest first, one page at a time.
   *
   * @param status       only return bookings in this status, or null for any
   * @param contactEmail only return bookings made with this email, or null for any
   * @param page         zero-based page number
   * @param size         number of bookings per page
   * @return the requested page of booking summaries, with the total number of matches and pages
   */
  PageResponse<BookingSummaryResponse> listBookings(BookingStatus status, String contactEmail, int page, int size);

  /**
   * Cancels a booking and releases its seats on the flight.
   *
   * @param reference the booking reference
   * @return the booking in status {@code CANCELLED}
   * @throws NotFoundException       if there is no booking with this reference
   * @throws InvalidRequestException if the booking is already cancelled or was changed by someone else at the same time
   */
  BookingResponse cancelBooking(String reference);
}
