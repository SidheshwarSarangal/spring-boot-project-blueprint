package com.example.starter.payment;

import jakarta.validation.constraints.NotBlank;

public record PaymentCommand(@NotBlank String reference) {
}
