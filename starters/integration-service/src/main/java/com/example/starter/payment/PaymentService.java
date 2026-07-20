package com.example.starter.payment;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final ProviderClient provider;

    public PaymentService(ProviderClient provider) {
        this.provider = provider;
    }

    public PaymentResult charge(PaymentCommand command) {
        return provider.charge(command);
    }
}
