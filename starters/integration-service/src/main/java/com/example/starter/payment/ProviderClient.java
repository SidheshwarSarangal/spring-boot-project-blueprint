package com.example.starter.payment;

public interface ProviderClient {
    PaymentResult charge(PaymentCommand command);
}
