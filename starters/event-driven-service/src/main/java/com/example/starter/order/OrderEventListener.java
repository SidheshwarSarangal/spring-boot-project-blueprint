package com.example.starter.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.messaging.enabled", havingValue = "true")
public class OrderEventListener {
    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);
    private final OrderService service;

    public OrderEventListener(OrderService service) {
        this.service = service;
    }

    @KafkaListener(topics = "orders.created")
    void receive(String eventId) {
        log.info("event={} firstProcessing={}", eventId, service.process(eventId));
    }
}
