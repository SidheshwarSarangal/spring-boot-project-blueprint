package com.example.starter;

import java.util.UUID;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {
    private static final String HEADER = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String incomingId = exchange.getRequest().getHeaders().getFirst(HEADER);
        String id = incomingId == null || incomingId.isBlank()
            ? UUID.randomUUID().toString()
            : incomingId;
        ServerWebExchange updated = exchange.mutate().request(request -> request.headers(headers -> {
            headers.set(HEADER, id);
        })).build();
        return chain.filter(updated);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
