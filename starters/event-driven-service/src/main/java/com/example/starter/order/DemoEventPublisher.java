package com.example.starter.order;

import java.util.UUID;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.messaging.enabled", havingValue = "true")
public class DemoEventPublisher implements ApplicationRunner {
    private final KafkaTemplate<String, String> kafka;

    public DemoEventPublisher(KafkaTemplate<String, String> kafka) {
        this.kafka = kafka;
    }

    @Override
    public void run(ApplicationArguments args) {
        String eventId = UUID.randomUUID().toString();
        kafka.send("orders.created", eventId, eventId);
    }
}
