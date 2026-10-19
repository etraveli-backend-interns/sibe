package com.etraveli.ibe.sibe.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PaymentRequest(
  @NotNull @DecimalMin("0.01") @Digits(integer = 8, fraction = 2) BigDecimal amount,
  @NotBlank @Pattern(regexp = "[A-Za-z]{3}", message = "must be a 3-letter currency code") String currency,
  @NotBlank @Pattern(regexp = "\\d{12,19}", message = "must be 12-19 digits") String cardNumber
) {}
