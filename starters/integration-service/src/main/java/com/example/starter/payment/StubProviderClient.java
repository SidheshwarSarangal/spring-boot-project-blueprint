package com.example.starter.payment;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.provider.mode", havingValue = "stub", matchIfMissing = true)
public class StubProviderClient implements ProviderClient {
    @Override
    public PaymentResult charge(PaymentCommand command) {
        return new PaymentResult(command.reference(), "APPROVED-STUB");
    }
}
