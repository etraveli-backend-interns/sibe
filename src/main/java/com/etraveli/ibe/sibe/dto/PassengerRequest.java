package com.etraveli.ibe.sibe.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PassengerRequest(
  @NotBlank @Size(max = 100) String firstName,
  @NotBlank @Size(max = 100) String lastName,
  @NotNull @Past LocalDate dateOfBirth,
  @NotBlank @Pattern(regexp = "[A-Za-z0-9]{6,20}", message = "must be 6-20 alphanumeric characters") String passportNumber
) {}
