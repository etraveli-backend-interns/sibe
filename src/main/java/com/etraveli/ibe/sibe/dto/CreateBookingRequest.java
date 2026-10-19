package com.etraveli.ibe.sibe.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateBookingRequest(
  @NotNull Long flightId,
  @NotBlank @Email @Size(max = 255) String contactEmail,
  @NotEmpty @Size(max = 9) List<@Valid PassengerRequest> passengers
) {}
