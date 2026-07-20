package com.example.starter.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@ConditionalOnProperty(name = "app.provider.mode", havingValue = "http")
public class HttpProviderAdapter implements ProviderClient {
    private final RestClient client;

    public HttpProviderAdapter(RestClient.Builder builder,
                               @Value("${app.provider.base-url}") String baseUrl) {
        this.client = builder.baseUrl(baseUrl).build();
    }

    @Override
    public PaymentResult charge(PaymentCommand command) {
        PaymentResult result = client.post()
            .uri("/payments")
            .body(command)
            .retrieve()
            .body(PaymentResult.class);
        if (result == null) {
            throw new IllegalStateException("Provider returned an empty response");
        }
        return result;
    }
}
