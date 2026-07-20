package com.example.starter.payment;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class PaymentServiceTest {
    @Test
    void returnsProviderResult() {
        ProviderClient provider = command -> new PaymentResult(command.reference(), "APPROVED");
        PaymentResult result = new PaymentService(provider).charge(new PaymentCommand("order-1"));
        assertThat(result.providerStatus()).isEqualTo("APPROVED");
    }
}
