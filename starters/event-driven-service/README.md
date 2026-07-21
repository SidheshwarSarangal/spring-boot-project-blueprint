# Event-driven service starter

[← Event-driven process](../../paths/event-driven-service.md) · [Adapt this starter](../../docs/starter-adaptation-guide.md)

The default mode builds and starts without a broker:

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

To exercise Kafka, start a local Kafka broker on `localhost:9092`, then run:

```bash
APP_MESSAGING_ENABLED=true ./mvnw spring-boot:run
```

The demo publishes one event, consumes it, and records the event ID so duplicate processing is ignored. Replace the in-memory ID set with durable inbox storage before production use.
